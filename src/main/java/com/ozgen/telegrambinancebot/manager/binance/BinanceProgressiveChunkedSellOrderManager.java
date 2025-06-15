package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.model.TradingStrategy;
import com.ozgen.telegrambinancebot.model.binance.AssetBalance;
import com.ozgen.telegrambinancebot.model.bot.ChunkOrder;
import com.ozgen.telegrambinancebot.model.bot.OrderStatus;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.InfoEvent;
import com.ozgen.telegrambinancebot.model.events.NewProgressiveChunkedSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.utils.PriceCalculator;
import com.ozgen.telegrambinancebot.utils.parser.GenericParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceProgressiveChunkedSellOrderManager {

    private final BinanceApiManager binanceApiManager;
    private final BotOrderService botOrderService;
    private final ApplicationEventPublisher publisher;
    private final BotConfiguration botConfiguration;
    private final BinanceHelper binanceHelper;

    @EventListener
    public void onNewSellProgressiveChunkOrderEvent(NewProgressiveChunkedSellOrderEvent event) {
        processChunkOrder(event.getChunkOrder());
    }

    public void processSellChunkOrders() {
        Date searchDate = this.binanceHelper.getSearchDate();
        List<OrderStatus> statuses = List.of(OrderStatus.SELL_PENDING_RETRY, OrderStatus.SELL_FAILED);
        List<ChunkOrder> orders = botOrderService.getBuyProgressiveChunksByStatusesAndDate(statuses, searchDate);
        log.info("Found {} progressive sell candidates", orders.size());
        orders.forEach(this::processChunkOrder);
    }

    private void processChunkOrder(ChunkOrder chunkOrder) {
        TradingSignal signal = chunkOrder.getTradingSignal();
        String symbol = chunkOrder.getSymbol();

        try {
            double currentPrice = GenericParser.getDouble(
                    binanceApiManager.getTickerPrice24(symbol).getLastPrice()
            ).orElseThrow();

            List<String> takeProfits = signal.getTakeProfits();
            double tpPrice;

            if (chunkOrder.getTakeProfitIndex() >= 0 && chunkOrder.getTakeProfitIndex() < takeProfits.size()) {
                tpPrice = GenericParser.getDouble(takeProfits.get(chunkOrder.getTakeProfitIndex())).orElse(0.0);
            } else {
                tpPrice = PriceCalculator.calculateCoinPriceInc(
                        chunkOrder.getBuyPrice(), botConfiguration.getProfitPercentage()
                );
            }

            boolean shouldSell = currentPrice >= tpPrice || signal.getStrategy() == TradingStrategy.DEFAULT;

            if (shouldSell) {
                double stopLoss = GenericParser.getDouble(signal.getStopLoss()).orElse(0.0);
                List<AssetBalance> assets = binanceHelper.getUserAssets();

                double sellAmount = binanceHelper.calculateSellAmount(assets, chunkOrder);
                if (sellAmount <= 0) {
                    log.warn("No available balance for chunk {}. Skipping sell.", chunkOrder.getId());
                    return;
                }

                chunkOrder.setSellCoinAmount(sellAmount);

                binanceApiManager.newOrderWithStopLoss(symbol, tpPrice, sellAmount, stopLoss);

                chunkOrder.setSellPrice(tpPrice);
                chunkOrder.setStopLoss(stopLoss);
                chunkOrder.setStatus(OrderStatus.SELL_EXECUTED);
                publisher.publishEvent(new InfoEvent(this, "Progressive sell executed: " + chunkOrder));
                log.info("Executed progressive sell for chunk {}", chunkOrder.getId());
            } else {
                chunkOrder.setStatus(OrderStatus.SELL_PENDING_RETRY);
                log.info("Chunk {} not sold. Current price ({}) below TP ({})",
                        chunkOrder.getId(), currentPrice, tpPrice);
            }

        } catch (Exception e) {
            chunkOrder.setStatus(OrderStatus.SELL_FAILED);
            publisher.publishEvent(new ErrorEvent(this, e));
            log.error("Error executing progressive sell for chunk {}: {}", chunkOrder.getId(), e.getMessage(), e);
        }

        botOrderService.saveChunkOrder(chunkOrder);
    }

    public void retryPendingChunks() {
        List<ChunkOrder> pending = botOrderService.getBuyChunksByStatus(OrderStatus.SELL_PENDING_RETRY);

        for (ChunkOrder chunk : pending) {
            try {
                double price = GenericParser.getDouble(
                        binanceApiManager.getTickerPrice24(chunk.getSymbol()).getLastPrice()
                ).orElseThrow();

                if (price > chunk.getBuyPrice()) {
                    log.info("Retrying progressive chunk {} due to rising price", chunk.getId());
                    publisher.publishEvent(new NewProgressiveChunkedSellOrderEvent(this, chunk));
                } else {
                    log.info("Progressive chunk {} still not profitable. Skipping.", chunk.getId());
                }
            } catch (Exception e) {
                log.error("Error retrying progressive chunk {}: {}", chunk.getId(), e.getMessage(), e);
            }
        }
    }
}
