package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.model.binance.OrderResponse;
import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.NewBuyOrderEvent;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.FutureTradeService;
import com.ozgen.telegrambinancebot.utils.PriceCalculator;
import com.ozgen.telegrambinancebot.utils.parser.GenericParser;
import com.ozgen.telegrambinancebot.utils.validators.TradingSignalValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinanceBuyOrderManager {

    private static final Logger log = LoggerFactory.getLogger(BinanceBuyOrderManager.class);

    private final BinanceApiManager binanceApiManager;
    private final ApplicationEventPublisher publisher;
    private final BotConfiguration botConfiguration;
    private final FutureTradeService futureTradeService;

    private final BotOrderService botOrderService;


    public void processNewBuyOrderEvent(NewBuyOrderEvent event) {
        TradingSignal tradingSignal = event.getTradingSignal();
        TickerData tickerData = event.getTickerData();

        try {
            SnapshotData accountSnapshot = this.binanceApiManager.getAccountSnapshot();
            Double btcToUsdRate = this.getBtcToUsdConversionRate();
            this.processOrder(tradingSignal, tickerData, accountSnapshot, btcToUsdRate);
        } catch (Exception e) {
            log.error("Error processing new buy order event for trading signal {}: {}", tradingSignal.getId(), e.getMessage(), e);
            this.futureTradeService.createFutureTrade(tradingSignal, TradeStatus.ERROR_BUY);
            this.processException(e);
        }
    }

    private void processOrder(TradingSignal tradingSignal, TickerData tickerData, SnapshotData accountSnapshot, Double btcToUsdRate) {
        if (!this.hasAccountEnoughBtc(accountSnapshot, btcToUsdRate)) {
            log.warn("Account does not have enough {} (less than {}$)", this.botConfiguration.getCurrency(), this.botConfiguration.getAmount());
            this.futureTradeService.createFutureTrade(tradingSignal, TradeStatus.INSUFFICIENT);
            return;
        }

        BuyOrder buyOrder = this.createBuyOrder(tradingSignal, tickerData, btcToUsdRate);
        if (buyOrder != null) {
            this.publishNewSellOrderEvent(buyOrder);
        }
    }

    private void publishNewSellOrderEvent(BuyOrder buyOrder) {
        NewSellOrderEvent newSellOrderEvent = new NewSellOrderEvent(this, buyOrder);
        this.publisher.publishEvent(newSellOrderEvent);
        log.info("Published NewSellOrderEvent for BuyOrder {}", buyOrder.getId());
    }


    private double calculateCoinAmount(double buyPrice, double btcPriceInUsd) {
        double dollarsToInvest = this.botConfiguration.getAmount();
        float binanceFeePercentage = this.botConfiguration.getBinanceFeePercentage();

        // Calculate how much BTC you can buy with $500
        double btcAmount = dollarsToInvest / btcPriceInUsd;

        double binanceFee = btcAmount * binanceFeePercentage;


        // Calculate how much coin you can buy with that BTC amount
        double coinAmount = (btcAmount - binanceFee) / buyPrice;

        return coinAmount;
    }

    private boolean hasAccountEnoughBtc(SnapshotData accountSnapshot, Double btcToUsdRate) {
        Double btcAmount = accountSnapshot.getCoinValue(this.botConfiguration.getCurrency());
        Double totalAccountValueInUsd = btcAmount * btcToUsdRate;
        return totalAccountValueInUsd >= this.botConfiguration.getAmount();
    }

    private Double getBtcToUsdConversionRate() throws Exception {
        TickerData tickerPrice24 = this.binanceApiManager.getTickerPrice24(this.botConfiguration.getCurrencyRate());
        return GenericParser.getDouble(tickerPrice24.getLastPrice()).get();
    }

    private BuyOrder createBuyOrder(TradingSignal tradingSignal, TickerData tickerData, double btcToUsdRate) {
        if (!this.isTradeSignalInRange(tradingSignal, tickerData)) {
            this.handleNotInRange(tradingSignal);
            return null;
        }

        BuyOrder buyOrder = this.initializeOrRetrieveBuyOrder(tradingSignal);
        this.populateBuyOrderDetails(buyOrder, tradingSignal, tickerData, btcToUsdRate);

        if (this.createOrderInBinance(buyOrder, tradingSignal)) {
            return this.saveBuyOrder(buyOrder);
        } else {
            return null;
        }
    }

    private boolean isTradeSignalInRange(TradingSignal tradingSignal, TickerData tickerData) {
        double buyPrice = PriceCalculator.calculateCoinPriceInc(GenericParser.getDouble(tickerData.getLastPrice()).get(),
                this.botConfiguration.getPercentageInc());
        return TradingSignalValidator.isAvailableToBuy(buyPrice, tradingSignal);
    }

    private void handleNotInRange(TradingSignal tradingSignal) {
        log.info("Trade signal not in range for buying: {}", tradingSignal.getId());
        this.futureTradeService.createFutureTrade(tradingSignal, TradeStatus.NOT_IN_RANGE);
    }

    private BuyOrder initializeOrRetrieveBuyOrder(TradingSignal tradingSignal) {
        BuyOrder buyOrder = this.botOrderService.getBuyOrder(tradingSignal).orElse(null);
        return (buyOrder != null) ? buyOrder : new BuyOrder();
    }

    private void populateBuyOrderDetails(BuyOrder buyOrder, TradingSignal tradingSignal, TickerData tickerData, double btcToUsdRate) {
        double buyPrice = PriceCalculator.calculateCoinPriceInc(GenericParser.getDouble(tickerData.getLastPrice()).get(), this.botConfiguration.getPercentageInc());
        double coinAmount = this.calculateCoinAmount(buyPrice, btcToUsdRate);
        double stopLoss = GenericParser.getDouble(tradingSignal.getEntryEnd()).get();

        buyOrder.setSymbol(tradingSignal.getSymbol());
        buyOrder.setCoinAmount(coinAmount);
        buyOrder.setStopLoss(stopLoss);
        buyOrder.setBuyPrice(buyPrice);
        buyOrder.setTimes(buyOrder.getTimes() + 1);
        tradingSignal.setIsProcessed(ProcessStatus.BUY);
        buyOrder.setTradingSignal(tradingSignal);
    }

    private boolean createOrderInBinance(BuyOrder buyOrder, TradingSignal tradingSignal) {
        try {
            log.info("Creating buy order for symbol {}", buyOrder.getSymbol());
            OrderResponse orderResponse = this.binanceApiManager.newOrder(buyOrder.getSymbol(), buyOrder.getBuyPrice(), buyOrder.getCoinAmount());
            log.info("Order created successfully: {}", orderResponse);
            return true;
        } catch (Exception e) {
            log.error("Failed to create buy order for symbol {}: {}", buyOrder.getSymbol(), e.getMessage(), e);
            this.futureTradeService.createFutureTrade(tradingSignal, TradeStatus.ERROR_BUY);
            this.processException(e);
            return false;
        }
    }

    private BuyOrder saveBuyOrder(BuyOrder buyOrder) {
        return this.botOrderService.createBuyOrder(buyOrder);
    }

    private void processException(Exception e) {
        ErrorEvent errorEvent = new ErrorEvent(this, e);
        this.publisher.publishEvent(errorEvent);
    }
}
