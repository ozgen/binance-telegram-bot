package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.configuration.properties.ScheduleConfiguration;
import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.model.binance.CancelAndNewOrderResponse;
import com.ozgen.telegrambinancebot.model.binance.OpenOrder;
import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.FutureTradeService;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import com.ozgen.telegrambinancebot.utils.DateFactory;
import com.ozgen.telegrambinancebot.utils.PriceCalculator;
import com.ozgen.telegrambinancebot.utils.SyncUtil;
import com.ozgen.telegrambinancebot.utils.parser.GenericParser;
import com.ozgen.telegrambinancebot.utils.validators.TradingSignalValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BinanceOpenBuyOrderManager {

    private static final Logger log = LoggerFactory.getLogger(BinanceOpenBuyOrderManager.class);

    private final BinanceApiManager binanceApiManager;
    private final TradingSignalService tradingSignalService;
    private final FutureTradeService futureTradeService;
    private final BotOrderService botOrderService;
    private final ApplicationEventPublisher publisher;
    private final ScheduleConfiguration scheduleConfiguration;
    private final BotConfiguration botConfiguration;

    public void processOpenBuyOrders() {
        Date searchDate = this.getSearchDate();
        List<TradingSignal> tradingSignals = this.tradingSignalService.getTradingSignalsAfterDateAndIsProcessIn(searchDate, List.of(ProcessStatus.SELL, ProcessStatus.BUY));

        for (TradingSignal tradingSignal : tradingSignals) {
            try {
                this.processOpenBuyOrder(tradingSignal);
                SyncUtil.pauseBetweenOperations();
            } catch (Exception e) {
                log.error("Error processing open buy order for trading signal {}: {}", tradingSignal.getId(), e.getMessage(), e);
                // Decide how to handle the error - log, alert, retry, etc.
                this.processException(e);
            }
        }
    }

    private void processOpenBuyOrder(TradingSignal tradingSignal) {
        String symbol = tradingSignal.getSymbol();
        try {
            List<OrderInfo> openOrders = this.binanceApiManager.getOpenOrders(symbol);
            TickerData tickerPrice24 = this.binanceApiManager.getTickerPrice24(symbol);

            if (!TradingSignalValidator.isAvailableToBuy(tickerPrice24, tradingSignal)) {
                this.handleNotAvailableToBuy(tradingSignal, symbol);
                return;
            }

            List<BuyOrder> buyOrders = this.processOpenOrders(tradingSignal, tickerPrice24, openOrders);
            this.publishNewSellOrderEvents(buyOrders);
        } catch (Exception e) {
            log.error("Error processing open buy order for symbol {}: {}", symbol, e.getMessage(), e);
            // Handle the exception as needed
            this.processException(e);
        }
    }

    private void handleNotAvailableToBuy(TradingSignal tradingSignal, String symbol) throws Exception {
        log.info("Symbol {} is not available to buy, cancelling orders.", symbol);
        List<OpenOrder> openOrderList = this.binanceApiManager.cancelOpenOrders(symbol);
        log.info("All orders have been canceled, orders: {}", openOrderList);
        this.futureTradeService.createFutureTrade(tradingSignal, TradeStatus.NOT_IN_RANGE);
    }

    private List<BuyOrder> processOpenOrders(TradingSignal tradingSignal, TickerData tickerPrice24, List<OrderInfo> openOrders) {
        return openOrders.stream()
                .map(orderInfo -> this.createCancelAndBuyOrder(tradingSignal, tickerPrice24, orderInfo))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void publishNewSellOrderEvents(List<BuyOrder> buyOrders) {
        buyOrders.forEach(buyOrder -> {
            NewSellOrderEvent newSellOrderEvent = new NewSellOrderEvent(this, buyOrder);
            this.publisher.publishEvent(newSellOrderEvent);
            log.info("Published NewSellOrderEvent for BuyOrder {}", buyOrder.getId());
        });
    }


    private Date getSearchDate() {
        return DateFactory.getDateBeforeInMonths(this.scheduleConfiguration.getMonthBefore());
    }


    private BuyOrder createCancelAndBuyOrder(TradingSignal tradingSignal, TickerData tickerData, OrderInfo orderInfo) {
        String symbol = tradingSignal.getSymbol();
        BuyOrder buyOrder = this.prepareBuyOrder(tradingSignal, tickerData, orderInfo, symbol);
        BuyOrder saved = this.processCancelAndNewOrder(tradingSignal, buyOrder, symbol, orderInfo);
        return saved;
    }

    private BuyOrder prepareBuyOrder(TradingSignal tradingSignal, TickerData tickerData, OrderInfo orderInfo, String symbol) {
        double coinAmount = GenericParser.getDouble(orderInfo.getOrigQty()).get() - GenericParser.getDouble(orderInfo.getExecutedQty()).get();
        double stopLossLimit = GenericParser.getDouble(tradingSignal.getEntryEnd()).get();
        double stopLoss = PriceCalculator.calculateCoinPriceDec(stopLossLimit, this.botConfiguration.getPercentageInc());
        double buyPrice = PriceCalculator.calculateCoinPriceInc(GenericParser.getDouble(tickerData.getLastPrice()).get(), this.botConfiguration.getPercentageInc());

        BuyOrder buyOrder = this.botOrderService.getBuyOrder(tradingSignal).orElse(null);
        if (buyOrder == null) {
            buyOrder = new BuyOrder();
        }
        this.updateBuyOrder(buyOrder, symbol, coinAmount, stopLoss, stopLossLimit, buyPrice, tradingSignal);
        return buyOrder;
    }

    private void updateBuyOrder(BuyOrder buyOrder, String symbol, double coinAmount, double stopLoss, double stopLossLimit, double buyPrice, TradingSignal tradingSignal) {
        buyOrder.setSymbol(symbol);
        buyOrder.setCoinAmount(coinAmount);
        buyOrder.setStopLoss(stopLoss);
        buyOrder.setStopLossLimit(stopLossLimit);
        buyOrder.setBuyPrice(buyPrice);
        buyOrder.setTimes(buyOrder.getTimes() + 1);
        tradingSignal.setIsProcessed(ProcessStatus.BUY);
        buyOrder.setTradingSignal(tradingSignal);
    }

    private BuyOrder processCancelAndNewOrder(TradingSignal tradingSignal, BuyOrder buyOrder, String symbol, OrderInfo orderInfo) {
        try {
            CancelAndNewOrderResponse response = this.binanceApiManager.cancelAndNewOrderWithStopLoss(symbol, buyOrder.getBuyPrice(), buyOrder.getCoinAmount(), buyOrder.getStopLoss(), buyOrder.getStopLossLimit(), orderInfo.getOrderId());
            log.info("Order cancel and created successfully: {}", response);
            return this.botOrderService.createBuyOrder(buyOrder);
        } catch (Exception e) {
            log.error("Failed to cancel and create order for symbol " + symbol, e);
            this.futureTradeService.createFutureTrade(tradingSignal, TradeStatus.ERROR_BUY);
            this.processException(e);
            return null;
        }
    }

    private void processException(Exception e) {
        ErrorEvent errorEvent = new ErrorEvent(this, e);
        this.publisher.publishEvent(errorEvent);
    }
}
