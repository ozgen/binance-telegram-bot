package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.AppConfiguration;
import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.model.binance.IntervalType;
import com.ozgen.telegrambinancebot.model.binance.KlineData;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.bot.ChunkOrder;
import com.ozgen.telegrambinancebot.model.bot.OrderStatus;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.InfoEvent;
import com.ozgen.telegrambinancebot.model.events.NewProgressiveChunkedBuyOrderEvent;
import com.ozgen.telegrambinancebot.model.events.NewProgressiveChunkedSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.utils.SyncUtil;
import com.ozgen.telegrambinancebot.utils.parser.GenericParser;
import jakarta.transaction.Transactional;
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
public class BinanceProgressiveChunkedBuyOrderManager {

    private final BinanceApiManager binanceApiManager;
    private final BotOrderService buyChunkOrderService;
    private final BinanceHelper binanceHelper;
    private final ApplicationEventPublisher publisher;
    private final BotConfiguration botConfiguration;
    private final AppConfiguration appConfiguration;

    @EventListener
    @Transactional
    public void handleProgressiveChunkedBuy(NewProgressiveChunkedBuyOrderEvent event) {
        TradingSignal signal = event.getTradingSignal();
        TickerData ticker = event.getTickerData();
        String symbol = signal.getSymbol();

        try {
            double currentPrice = GenericParser.getDouble(ticker.getLastPrice()).orElseThrow();

            List<KlineData> klines = binanceApiManager.getListOfKlineData(symbol, IntervalType.FIVE_MINUTES);
            double avgQuoteVolume = klines.stream()
                    .mapToDouble(KlineData::getQuoteAssetVolume)
                    .average()
                    .orElse(0.0);

            if (avgQuoteVolume < appConfiguration.getMinQuoteVolume()) {
                log.warn("Low liquidity for {} (avg quote volume: {}). Skipping.", symbol, avgQuoteVolume);
                return;
            }

            int maxChunks = this.binanceHelper.calculateDynamicChunkCount(avgQuoteVolume);

            List<ChunkOrder> executedChunks = buyChunkOrderService
                    .getChunksBySignalAndStatuses(signal.getId(), List.of(OrderStatus.BUY_EXECUTED));

            if (executedChunks.size() >= maxChunks) {
                log.info("All chunks already placed for signal {}", signal.getId());
                return;
            }

            if (shouldPlaceNextChunk(signal, currentPrice, klines)) {
                log.info("Market looks good. Placing next chunk for {}", symbol);
                placeNextChunk(signal, currentPrice, executedChunks.size(), maxChunks);
            } else {
                log.info("Conditions not favorable yet for {}", symbol);
            }

        } catch (Exception e) {
            log.error("Error in progressive chunk buy for {}: {}", signal.getSymbol(), e.getMessage(), e);
            publisher.publishEvent(new ErrorEvent(this, e));
        }
    }

    public void processFailedProgressiveBuyChunks() {
        Date searchDate = binanceHelper.getSearchDate();
        List<OrderStatus> statuses = List.of(OrderStatus.BUY_FAILED);
        List<ChunkOrder> orders = buyChunkOrderService.getBuyProgressiveChunksByStatusesAndDate(
                statuses, searchDate);

        log.info("Found {} executed progressive buy chunks", orders.size());
        orders.forEach(order -> {
            if (shouldRetryFailedChunk(order)) {
                publisher.publishEvent(new NewProgressiveChunkedSellOrderEvent(this, order));
            }
        });
    }

    public void processExecutedProgressiveBuyChunks() {
        Date searchDate = binanceHelper.getSearchDate();
        List<OrderStatus> statuses = List.of(OrderStatus.BUY_EXECUTED);
        List<ChunkOrder> orders = buyChunkOrderService.getBuyProgressiveChunksByStatusesAndDate(
                statuses, searchDate);

        log.info("Found {} executed progressive buy chunks", orders.size());
        orders.forEach(order -> {
            if (shouldTriggerNextChunk(order)) {
                publisher.publishEvent(new NewProgressiveChunkedSellOrderEvent(this, order));
            }
        });
    }

    public boolean shouldTriggerNextChunk(ChunkOrder chunkOrder) {
        TradingSignal signal = chunkOrder.getTradingSignal();
        String symbol = chunkOrder.getSymbol();

        // If already the last chunk, don't trigger more
        if (chunkOrder.getChunkIndex() >= chunkOrder.getTotalChunkCount() - 1) {
            log.info("Chunk {} is the last one. No further chunks to trigger.", chunkOrder.getId());
            return false;
        }

        try {
            double currentPrice = GenericParser.getDouble(
                    binanceApiManager.getTickerPrice24(symbol).getLastPrice()
            ).orElseThrow();

            double entryStart = GenericParser.getDouble(signal.getEntryStart()).orElse(0.0);
            double entryEnd = GenericParser.getDouble(signal.getEntryEnd()).orElse(Double.MAX_VALUE);

            // Trigger next chunk if price is still in entry zone
            boolean inEntryZone = currentPrice >= entryStart && currentPrice <= entryEnd;

            if (!inEntryZone) {
                log.info("Current price {} not in entry range ({} - {}) for symbol {}",
                        currentPrice, entryStart, entryEnd, symbol);
            }

            return inEntryZone;
        } catch (Exception e) {
            log.error("Failed to determine if next chunk should be triggered for chunk {}: {}", chunkOrder.getId(), e.getMessage(), e);
            return false;
        }
    }

    public boolean shouldRetryFailedChunk(ChunkOrder chunkOrder) {
        TradingSignal signal = chunkOrder.getTradingSignal();
        String symbol = chunkOrder.getSymbol();

        try {
            double currentPrice = GenericParser.getDouble(
                    binanceApiManager.getTickerPrice24(symbol).getLastPrice()
            ).orElseThrow();

            double entryStart = GenericParser.getDouble(signal.getEntryStart()).orElse(0.0);
            double entryEnd = GenericParser.getDouble(signal.getEntryEnd()).orElse(Double.MAX_VALUE);

            boolean inEntryZone = currentPrice >= entryStart && currentPrice <= entryEnd;

            if (!inEntryZone) {
                log.info("Retry skipped for chunk {}: price {} out of entry range ({} - {})",
                        chunkOrder.getId(), currentPrice, entryStart, entryEnd);
            }

            return inEntryZone;
        } catch (Exception e) {
            log.error("Error while checking retry condition for chunk {}: {}", chunkOrder.getId(), e.getMessage(), e);
            return false;
        }
    }

    private boolean shouldPlaceNextChunk(TradingSignal signal, double currentPrice, List<KlineData> klines) {
        double entryStart = GenericParser.getDouble(signal.getEntryStart()).orElseThrow();
        double entryEnd = GenericParser.getDouble(signal.getEntryEnd()).orElseThrow();

        boolean inEntryRange = currentPrice >= entryEnd && currentPrice <= entryStart;
        boolean shortTermBullish = binanceHelper.isShortTermBullish(klines);

        return inEntryRange && shortTermBullish;
    }

    private void placeNextChunk(TradingSignal signal, double currentPrice, int chunkIndex, int maxChunks) {
        double stopLoss = GenericParser.getDouble(signal.getStopLoss()).orElseThrow();
        double leverage = currentPrice / (currentPrice - stopLoss);

        double totalInvestment = botConfiguration.getAmount();
        double investPerChunk = totalInvestment / maxChunks;
        double coinAmount = investPerChunk / currentPrice;

        ChunkOrder chunkOrder = new ChunkOrder();
        chunkOrder.setSymbol(signal.getSymbol());
        chunkOrder.setChunkIndex(chunkIndex);
        chunkOrder.setTotalChunkCount(maxChunks);
        chunkOrder.setBuyCoinAmount(coinAmount);
        chunkOrder.setBuyPrice(currentPrice);
        chunkOrder.setStopLoss(stopLoss);
        chunkOrder.setTradingSignal(signal);
        chunkOrder.setEntryPoint(currentPrice);
        chunkOrder.setLeverage(leverage);
        chunkOrder.setTakeProfitIndex(chunkIndex % signal.getTakeProfits().size());
        chunkOrder.setTakeProfitAllocation(100 / maxChunks);

        try {
            binanceApiManager.newOrder(signal.getSymbol(), currentPrice, coinAmount);
            chunkOrder.setStatus(OrderStatus.BUY_EXECUTED);
        } catch (Exception e) {
            chunkOrder.setStatus(OrderStatus.BUY_FAILED);
            publisher.publishEvent(new ErrorEvent(this, e));
        }

        ChunkOrder saved = buyChunkOrderService.saveChunkOrder(chunkOrder);
        publisher.publishEvent(new InfoEvent(this, "Saved progressive BuyChunkOrder: " + saved));
        publisher.publishEvent(new NewProgressiveChunkedSellOrderEvent(this, chunkOrder));
        SyncUtil.pauseBetweenOperations();
    }
}
