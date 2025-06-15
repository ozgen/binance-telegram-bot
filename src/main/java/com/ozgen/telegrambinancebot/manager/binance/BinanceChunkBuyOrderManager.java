package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.AppConfiguration;
import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.model.binance.AssetBalance;
import com.ozgen.telegrambinancebot.model.binance.IntervalType;
import com.ozgen.telegrambinancebot.model.binance.KlineData;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.bot.ChunkOrder;
import com.ozgen.telegrambinancebot.model.bot.OrderStatus;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.InfoEvent;
import com.ozgen.telegrambinancebot.model.events.NewChunkedBuyOrderEvent;
import com.ozgen.telegrambinancebot.model.events.NewSellChunkOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import com.ozgen.telegrambinancebot.utils.PriceCalculator;
import com.ozgen.telegrambinancebot.utils.SyncUtil;
import com.ozgen.telegrambinancebot.utils.parser.GenericParser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceChunkBuyOrderManager {

    private final BinanceApiManager binanceApiManager;
    private final BotOrderService buyChunkOrderService;
    private final BinanceHelper binanceHelper;
    private final ApplicationEventPublisher publisher;
    private final BotConfiguration botConfiguration;
    private final AppConfiguration appConfiguration;
    private final TradingSignalService tradingSignalService;

    @Transactional
    public void handleChunkedBuyEvent(NewChunkedBuyOrderEvent event) {
        TickerData ticker = event.getTickerData();
        executeBuyChunks(event.getTradingSignal(), ticker, true);
    }

    public void processFailedBuyChunkOrders() {
        Date searchDate = this.binanceHelper.getSearchDate();
        List<OrderStatus> statuses = List.of(OrderStatus.BUY_FAILED);
        List<ChunkOrder> orders = this.buyChunkOrderService.getBuyChunksByStatusesAndDate(statuses, searchDate);
        log.info("Found {} orders", orders.size());
        orders.forEach(this::handleChunkOrder);
        log.info("BuyChunkOrders have been processed");
    }

    public void processExecutedBuyChunkOrders (){
        Date searchDate = this.binanceHelper.getSearchDate();
        List<OrderStatus> statuses = List.of(OrderStatus.BUY_EXECUTED);
        List<ChunkOrder> orders = this.buyChunkOrderService.getBuyChunksByStatusesAndDate(statuses, searchDate);
        log.info("Found {} orders", orders.size());
        orders.forEach(chunkOrder -> this.publisher.publishEvent(new NewSellChunkOrderEvent(this, chunkOrder)));
        log.info("ExecutedBuyChunkOrders have been processed");
    }

    @Transactional
    public void handleChunkOrder(ChunkOrder chunkOrder) {
        try {
            TickerData ticker = this.binanceApiManager.getTickerPrice24(chunkOrder.getSymbol());
            executeBuyChunks(chunkOrder.getTradingSignal(), ticker, false, chunkOrder);
        } catch (Exception e) {
            log.error("Failed to get ticker price for {}: {}", chunkOrder.getSymbol(), e.getMessage(), e);
            this.publisher.publishEvent(new ErrorEvent(this, e));
        }
    }

    protected void executeBuyChunks(TradingSignal signal, TickerData ticker, boolean isInitialBuy) {
        executeBuyChunks(signal, ticker, isInitialBuy, null);
    }

    protected void executeBuyChunks(TradingSignal signal, TickerData ticker, boolean isInitialBuy, ChunkOrder providedChunk) {
        String symbol = signal.getSymbol();
        try {
            List<AssetBalance> assets = this.binanceHelper.getUserAssets();
            if (!this.binanceHelper.hasAccountEnoughAsset(assets, signal)) {
                log.warn("Not enough {} (less than {}$)", this.botConfiguration.getCurrency(),
                        this.botConfiguration.getAmount());
                return;
            }

            double lastPrice = GenericParser.getDouble(ticker.getLastPrice()).orElseThrow();
            List<KlineData> klines = this.binanceApiManager.getListOfKlineData(symbol, IntervalType.FIVE_MINUTES);
            double avgQuoteVolume = klines.stream()
                    .mapToDouble(KlineData::getQuoteAssetVolume)
                    .average()
                    .orElse(0.0);

            if (avgQuoteVolume < this.appConfiguration.getMinQuoteVolume()) {
                log.warn("Low liquidity for {} (avg quote volume: {}). Skipping.", symbol, avgQuoteVolume);
                return;
            }

            if (isInitialBuy) {
                int chunkCount = this.binanceHelper.calculateDynamicChunkCount(avgQuoteVolume);
                double totalInvestment = this.botConfiguration.getAmount();
                double investPerChunk = totalInvestment / chunkCount;
                double stopLoss = GenericParser.getDouble(signal.getEntryEnd()).orElseThrow();

                for (int i = 0; i < chunkCount; i++) {
                    double buyPrice = PriceCalculator.calculateCoinPriceInc(lastPrice, this.botConfiguration.getPercentageInc());
                    double coinAmount = investPerChunk / buyPrice;

                    ChunkOrder chunkOrder = new ChunkOrder();
                    chunkOrder.setSymbol(symbol);
                    chunkOrder.setChunkIndex(i);
                    chunkOrder.setTotalChunkCount(chunkCount);
                    chunkOrder.setBuyCoinAmount(coinAmount);
                    chunkOrder.setBuyPrice(buyPrice);
                    chunkOrder.setStopLoss(stopLoss);
                    chunkOrder.setTradingSignal(signal);

                    placeChunkOrder(chunkOrder);
                    this.publisher.publishEvent(new NewSellChunkOrderEvent(this, chunkOrder));
                    log.info("Published NewSellChunkOrderEvent for chunk {}", chunkOrder.getId());
                    SyncUtil.pauseBetweenOperations();
                }
            } else if (providedChunk != null) {
                double buyPrice = PriceCalculator.calculateCoinPriceInc(lastPrice, this.botConfiguration.getPercentageInc());
                providedChunk.setBuyPrice(buyPrice);
                placeChunkOrder(providedChunk);
                this.publisher.publishEvent(new NewSellChunkOrderEvent(this, providedChunk));
                log.info("Published NewSellChunkOrderEvent for chunk {}", providedChunk.getId());
                SyncUtil.pauseBetweenOperations();
            }

            signal.setIsProcessed(ProcessStatus.DONE);
            this.tradingSignalService.updateTradingSignal(signal);

        } catch (Exception e) {
            log.error("Error in executeBuyChunks for {}: {}", symbol, e.getMessage(), e);
            this.publisher.publishEvent(new ErrorEvent(this, e));
        }
    }

    protected void placeChunkOrder(ChunkOrder chunkOrder) {
        try {
            this.binanceApiManager.newOrder(chunkOrder.getSymbol(), chunkOrder.getBuyPrice(), chunkOrder.getBuyCoinAmount());
            chunkOrder.setStatus(OrderStatus.BUY_EXECUTED);
            log.info("Executed chunk buy order: {}", chunkOrder);
        } catch (Exception e) {
            chunkOrder.setStatus(OrderStatus.BUY_FAILED);
            log.error("Failed to execute chunk buy: {}", e.getMessage(), e);
            this.publisher.publishEvent(new ErrorEvent(this, e));
        }

        ChunkOrder saved = buyChunkOrderService.saveChunkOrder(chunkOrder);
        this.publisher.publishEvent(new InfoEvent(this, "Saved BuyChunkOrder: " + saved));
    }
}
