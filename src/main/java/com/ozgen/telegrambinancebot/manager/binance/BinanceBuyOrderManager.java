package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.model.binance.OrderResponse;
import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.bot.FutureTrade;
import com.ozgen.telegrambinancebot.model.events.NewBuyOrderEvent;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.FutureTradeService;
import com.ozgen.telegrambinancebot.utils.PriceCalculator;
import com.ozgen.telegrambinancebot.utils.parser.GenericParser;
import com.ozgen.telegrambinancebot.utils.validators.TradingSignalValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class BinanceBuyOrderManager {

    private static final Logger log = LoggerFactory.getLogger(BinanceBuyOrderManager.class);

    private final BinanceApiManager binanceApiManager;
    private final ApplicationEventPublisher publisher;
    private final BotConfiguration botConfiguration;
    private final FutureTradeService futureTradeService;

    private final BotOrderService botOrderService;


    public BinanceBuyOrderManager(BinanceApiManager binanceApiManager, ApplicationEventPublisher publisher,
                                  BotConfiguration botConfiguration, FutureTradeService futureTradeService,
                                  BotOrderService botOrderService) {
        this.binanceApiManager = binanceApiManager;
        this.publisher = publisher;
        this.botConfiguration = botConfiguration;
        this.futureTradeService = futureTradeService;
        this.botOrderService = botOrderService;

    }

    public void processNewBuyOrderEvent(NewBuyOrderEvent event) {
        TradingSignal tradingSignal = event.getTradingSignal();
        TickerData tickerData = event.getTickerData();
        SnapshotData accountSnapshot = null;
        Double btcToUsdRate = null;
        try {
            accountSnapshot = this.binanceApiManager.getAccountSnapshot();
            btcToUsdRate = this.getBtcToUsdConversionRate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        boolean hasAccountEnoughBtc = this.hasAccountEnoughBtc(accountSnapshot, btcToUsdRate);
        if (!hasAccountEnoughBtc) {
            log.warn("the account has not enough amount of {} less than {}$.", this.botConfiguration.getCurrency(),
                    this.botConfiguration.getAmount());
            FutureTrade futureTrade = new FutureTrade();
            futureTrade.setTradeStatus(TradeStatus.INSUFFICIENT);
            futureTrade.setTradeSignalId(tradingSignal.getId());
            this.futureTradeService.createFutureTrade(futureTrade);
            return;
        }

        BuyOrder buyOrder = this.createBuyOrder(tradingSignal, tickerData, btcToUsdRate);
        if (buyOrder != null) {
            NewSellOrderEvent newSellOrderEvent = new NewSellOrderEvent(this, buyOrder);
            this.publisher.publishEvent(newSellOrderEvent);
            log.info("NewSellOrderEvent published successfully. NewSellOrderEvent: {}", newSellOrderEvent);
        }
    }

    private double calculateCoinAmount(TickerData tickerData, double btcPriceInUsd) {
        double dollarsToInvest = this.botConfiguration.getAmount();

        // Convert the lastPrice from String to Double
        double coinToBtcRate = Double.parseDouble(tickerData.getLastPrice());

        // Calculate how much BTC you can buy with $500
        double btcAmount = dollarsToInvest / btcPriceInUsd;

        // Calculate how much coin you can buy with that BTC amount
        double coinAmount = btcAmount / coinToBtcRate;

        return coinAmount;
    }

    private boolean hasAccountEnoughBtc(SnapshotData accountSnapshot, Double btcToUsdRate) {
        Double btcAmount = accountSnapshot.getCoinValue(this.botConfiguration.getCurrency());
        Double totalAccountValueInUsd = btcAmount * btcToUsdRate;
        return totalAccountValueInUsd >= this.botConfiguration.getAmount();
    }

    private Double getBtcToUsdConversionRate() throws Exception {
        TickerData tickerPrice24 = this.binanceApiManager.getTickerPrice24(this.botConfiguration.getCurrencyRate());
        return GenericParser.getDouble(tickerPrice24.getLastPrice());
    }

    private BuyOrder createBuyOrder(TradingSignal tradingSignal, TickerData tickerData, double btcToUsdRate) {
        double coinAmount = this.calculateCoinAmount(tickerData, btcToUsdRate);
        double stopLossLimit = GenericParser.getDouble(tradingSignal.getEntryEnd());
        double stopLoss = PriceCalculator.calculateCoinPriceDec(stopLossLimit, this.botConfiguration.getPercentageInc());
        double buyPrice = PriceCalculator.calculateCoinPriceInc(GenericParser.getDouble(tickerData.getLastPrice()),
                this.botConfiguration.getPercentageInc());
        String symbol = tradingSignal.getSymbol();

        boolean availableToBuy = TradingSignalValidator.isAvailableToBuy(buyPrice, tradingSignal);

        if (!availableToBuy) {
            log.info("Trade signal not in range for buying: {}", tradingSignal.getId());
            FutureTrade futureTrade = new FutureTrade();
            futureTrade.setTradeStatus(TradeStatus.NOT_IN_RANGE);
            futureTrade.setTradeSignalId(tradingSignal.getId());
            this.futureTradeService.createFutureTrade(futureTrade);
            return null;
        }

        BuyOrder buyOrder = this.botOrderService.getBuyOrder(tradingSignal);

        if (buyOrder == null) {
            buyOrder = new BuyOrder();
        }
        buyOrder.setSymbol(symbol);
        buyOrder.setCoinAmount(coinAmount);
        buyOrder.setStopLoss(stopLoss);
        buyOrder.setStopLossLimit(stopLossLimit);
        buyOrder.setBuyPrice(buyPrice);
        buyOrder.setTimes(buyOrder.getTimes() + 1);
        tradingSignal.setIsProcessed(ProcessStatus.BUY);
        buyOrder.setTradingSignal(tradingSignal);
        buyOrder = this.botOrderService.createBuyOrder(buyOrder);

        log.info("Starting to create buy orders for symbol {}", symbol);
        try {
            log.info("Creating order  for symbol {}", symbol);
            OrderResponse orderResponse = this.binanceApiManager.newOrderWithStopLoss(symbol, buyPrice, coinAmount, stopLoss, stopLossLimit);
            log.info("Order created successfully: {}", orderResponse);
        } catch (Exception e) {
            log.error("Failed to create order number for symbol " + symbol, e);
            FutureTrade futureTrade = new FutureTrade();
            futureTrade.setTradeStatus(TradeStatus.ERROR_BUY);
            futureTrade.setTradeSignalId(tradingSignal.getId());
            this.futureTradeService.createFutureTrade(futureTrade);
        }
        log.info("Order creation process completed for symbol {}", symbol);
        return buyOrder;
    }
}
