package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.model.TradingStrategy;
import com.ozgen.telegrambinancebot.model.binance.AssetBalance;
import com.ozgen.telegrambinancebot.model.bot.ChunkOrder;
import com.ozgen.telegrambinancebot.model.bot.OrderStatus;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.InfoEvent;
import com.ozgen.telegrambinancebot.model.events.NewSellChunkOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.utils.PriceCalculator;
import com.ozgen.telegrambinancebot.utils.SymbolGenerator;
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
public class BinanceChunkSellOrderManager {

    private final BinanceApiManager binanceApiManager;
    private final BotOrderService botOrderService;
    private final ApplicationEventPublisher publisher;
    private final BotConfiguration botConfiguration;
    private final BinanceHelper binanceHelper;

    @EventListener
    public void onNewSellChunkOrderEvent(NewSellChunkOrderEvent event) {
        processChunkOrder(event.getChunkOrder());
    }

    public void processSellChunkOrders() {
        Date searchDate = this.binanceHelper.getSearchDate();
        List<OrderStatus> statuses = List.of(OrderStatus.SELL_PENDING_RETRY, OrderStatus.SELL_FAILED);
        List<ChunkOrder> orders = this.botOrderService.getBuyChunksByStatusesAndDate(statuses, searchDate);
        log.info("Found {} orders", orders.size());
        orders.forEach(this::handleChunkOrder);
        log.info("SellChunkOrders have been processed");
    }

    public void handleChunkOrder(ChunkOrder chunkOrder) {
        processChunkOrder(chunkOrder);
    }

    private void processChunkOrder(ChunkOrder chunkOrder) {
        TradingSignal signal = chunkOrder.getTradingSignal();
        String symbol = chunkOrder.getSymbol();

        try {
            double currentPrice = GenericParser.getDouble(
                    binanceApiManager.getTickerPrice24(symbol).getLastPrice()).orElseThrow();

            double sellPrice = PriceCalculator.calculateCoinPriceInc(
                    chunkOrder.getBuyPrice(), botConfiguration.getProfitPercentage());

            boolean shouldSell = currentPrice >= sellPrice || signal.getStrategy() == TradingStrategy.DEFAULT;

            if (shouldSell) {
                double stopLoss = GenericParser.getDouble(signal.getStopLoss()).orElse(0.0);
                List<AssetBalance> assets = binanceHelper.getUserAssets();
                String coinSymbol = SymbolGenerator.getCoinSymbol(symbol, botConfiguration.getCurrency());

                if (coinSymbol == null) {
                    log.error("Coin symbol could not be generated for BuyOrder ID {}", chunkOrder.getId());
                    return;
                }

                Double coinAmount = GenericParser.getAssetFromSymbol(assets, coinSymbol);
                if (coinAmount == null) {
                    log.error("Coin amount could not be found for symbol {}", coinSymbol);
                    return;
                }

                chunkOrder.setSellCoinAmount(
                        coinAmount > chunkOrder.getBuyCoinAmount() ?
                                chunkOrder.getBuyCoinAmount() : coinAmount
                );

                binanceApiManager.newOrderWithStopLoss(symbol, sellPrice,
                        chunkOrder.getSellCoinAmount(), stopLoss);

                chunkOrder.setSellPrice(sellPrice);
                chunkOrder.setStopLoss(stopLoss);
                chunkOrder.setStatus(OrderStatus.SELL_EXECUTED);
                log.info("Sell chunk executed: {}", chunkOrder);
                publisher.publishEvent(new InfoEvent(this, "Sell executed for: " + chunkOrder));
            } else {
                chunkOrder.setStatus(OrderStatus.SELL_PENDING_RETRY);
                log.info("Chunk {} not sold, price below threshold. Marked for retry.", chunkOrder.getId());
            }
        } catch (Exception e) {
            log.error("Error executing sell chunk {}: {}", chunkOrder.getId(), e.getMessage(), e);
            chunkOrder.setStatus(OrderStatus.SELL_FAILED);
            publisher.publishEvent(new ErrorEvent(this, e));
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
                    log.info("Retrying chunk {} due to rising price", chunk.getId());
                    publisher.publishEvent(new NewSellChunkOrderEvent(this, chunk));
                } else {
                    log.info("Chunk {} still not profitable. Waiting...", chunk.getId());
                }
            } catch (Exception e) {
                log.error("Failed to retry chunk {}: {}", chunk.getId(), e.getMessage(), e);
            }
        }
    }
}
