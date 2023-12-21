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
import com.ozgen.telegrambinancebot.model.bot.FutureTrade;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.FutureTradeService;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import com.ozgen.telegrambinancebot.utils.DateFactory;
import com.ozgen.telegrambinancebot.utils.PriceCalculator;
import com.ozgen.telegrambinancebot.utils.parser.GenericParser;
import com.ozgen.telegrambinancebot.utils.validators.TradingSignalValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class BinanceOpenBuyOrderManager {

    private static final Logger log = LoggerFactory.getLogger(BinanceOpenBuyOrderManager.class);

    private final BinanceApiManager binanceApiManager;
    private final TradingSignalService tradingSignalService;
    private final FutureTradeService futureTradeService;
    private final BotOrderService botOrderService;
    private final ApplicationEventPublisher publisher;
    private final ScheduleConfiguration scheduleConfiguration;
    private final BotConfiguration botConfiguration;


    public BinanceOpenBuyOrderManager(BinanceApiManager binanceApiManager, TradingSignalService tradingSignalService, FutureTradeService futureTradeService, BotOrderService botOrderService, ApplicationEventPublisher publisher, ScheduleConfiguration scheduleConfiguration, BotConfiguration botConfiguration) {
        this.binanceApiManager = binanceApiManager;
        this.tradingSignalService = tradingSignalService;
        this.futureTradeService = futureTradeService;
        this.botOrderService = botOrderService;
        this.publisher = publisher;
        this.scheduleConfiguration = scheduleConfiguration;
        this.botConfiguration = botConfiguration;
    }

    public void processOpenBuyOrders() {
        Date searchDate = this.getSearchDate();
        List<TradingSignal> tradingSignals = this.tradingSignalService.getTradingSignalsAfterDateAndIsProcessIn(searchDate, List.of(ProcessStatus.SELL, ProcessStatus.BUY));
        tradingSignals.parallelStream()
                .forEach(tradingSignal -> {
                    try {
                        this.processOpenBuyOrder(tradingSignal);
                        TimeUnit.SECONDS.sleep(5);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private void processOpenBuyOrder(TradingSignal tradingSignal) throws Exception {
        String symbol = tradingSignal.getSymbol();
        List<OrderInfo> openOrders = this.binanceApiManager.getOpenOrders(symbol);
        TickerData tickerPrice24 = this.binanceApiManager.getTickerPrice24(symbol);
        boolean availableToBuy = TradingSignalValidator.isAvailableToBuy(tickerPrice24, tradingSignal);
        if (!availableToBuy) {
            log.info("this {} symbol is not available to buy.", symbol);
            List<OpenOrder> openOrderList = this.binanceApiManager.cancelOpenOrders(symbol);
            log.info("All orders have been canceled, orders: {}", openOrderList);
            FutureTrade futureTrade = new FutureTrade();
            futureTrade.setTradeStatus(TradeStatus.NOT_IN_RANGE);
            futureTrade.setTradeSignalId(tradingSignal.getId());
            this.futureTradeService.createFutureTrade(futureTrade);
            return;
        }
        List<BuyOrder> buyOrders = openOrders.stream()
                .map(orderInfo -> this.createCancelAndBuyOrder(tradingSignal, tickerPrice24, orderInfo))
                .filter(buyOrder -> buyOrder != null)
                .collect(Collectors.toList());
        if(!buyOrders.isEmpty()){
           buyOrders.forEach(buyOrder -> {
               NewSellOrderEvent newSellOrderEvent = new NewSellOrderEvent(this, buyOrder);
               this.publisher.publishEvent(newSellOrderEvent);
               log.info("NewSellOrderEvent published successfully. NewSellOrderEvent: {}", newSellOrderEvent);
           });
        }
    }

    private Date getSearchDate() {
        return DateFactory.getDateBeforeInMonths(this.scheduleConfiguration.getMonthBefore());
    }


    private BuyOrder createCancelAndBuyOrder(TradingSignal tradingSignal, TickerData tickerData, OrderInfo orderInfo) {
        Double executedCount = GenericParser.getDouble(orderInfo.getExecutedQty());
        Double totalBuyCountCount = GenericParser.getDouble(orderInfo.getOrigQty());
        double coinAmount = totalBuyCountCount - executedCount;
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

        log.info("Starting to create cancel and buy orders for symbol {}", symbol);
        try {
            log.info("Cancel and creating order  for symbol {}", symbol);
            CancelAndNewOrderResponse cancelAndNewOrderResponse = this.binanceApiManager.cancelAndNewOrderWithStopLoss(symbol, buyPrice, coinAmount, stopLoss, stopLossLimit, orderInfo.getOrderId());
            log.info("Order cancel and created successfully: {}", cancelAndNewOrderResponse);
        } catch (Exception e) {
            log.error("Failed to create order number for symbol " + symbol, e);
            FutureTrade futureTrade = new FutureTrade();
            futureTrade.setTradeStatus(TradeStatus.ERROR_BUY);
            futureTrade.setTradeSignalId(tradingSignal.getId());
            this.futureTradeService.createFutureTrade(futureTrade);
            return null;
        }
        log.info("Order creation process completed for symbol {}", symbol);
        return buyOrder;
    }
}
