package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.configuration.properties.ScheduleConfiguration;
import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.FutureTradeService;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import com.ozgen.telegrambinancebot.utils.DateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class BinanceOpenSellOrderManager {

    private static final Logger log = LoggerFactory.getLogger(BinanceOpenSellOrderManager.class);

    private final BinanceApiManager binanceApiManager;
    private final TradingSignalService tradingSignalService;
    private final FutureTradeService futureTradeService;
    private final BotOrderService botOrderService;
    private final ApplicationEventPublisher publisher;
    private final ScheduleConfiguration scheduleConfiguration;
    private final BotConfiguration botConfiguration;


    public BinanceOpenSellOrderManager(BinanceApiManager binanceApiManager, TradingSignalService tradingSignalService, FutureTradeService futureTradeService, BotOrderService botOrderService, ApplicationEventPublisher publisher, ScheduleConfiguration scheduleConfiguration, BotConfiguration botConfiguration) {
        this.binanceApiManager = binanceApiManager;
        this.tradingSignalService = tradingSignalService;
        this.futureTradeService = futureTradeService;
        this.botOrderService = botOrderService;
        this.publisher = publisher;
        this.scheduleConfiguration = scheduleConfiguration;
        this.botConfiguration = botConfiguration;
    }

    public void processOpenSellOrders() {
        Date searchDate = getSearchDate();
        List<TradingSignal> tradingSignals = tradingSignalService.getTradingSignalsAfterDateAndIsProcessIn(searchDate, List.of(ProcessStatus.SELL));
        List<BuyOrder> buyOrders = botOrderService.getBuyOrders(tradingSignals);

        buyOrders.forEach(this::safelyPublishNewSellOrder);
    }

    private void safelyPublishNewSellOrder(BuyOrder buyOrder) {
        try {
            this.publishNewSellOrder(buyOrder);
            this.pauseBetweenOperations();
        } catch (Exception e) {
            log.error("Error while processing sell order for BuyOrder ID {}: {}", buyOrder.getId(), e.getMessage(), e);
            // Handle the exception as needed
        }
    }

    private void publishNewSellOrder(BuyOrder buyOrder) {
        NewSellOrderEvent newSellOrderEvent = new NewSellOrderEvent(this, buyOrder);
        publisher.publishEvent(newSellOrderEvent);
        log.info("Published NewSellOrderEvent for BuyOrder {}", buyOrder.getId());
    }

    private void pauseBetweenOperations() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted during pause", e);
        }
    }

    private Date getSearchDate() {
        return DateFactory.getDateBeforeInMonths(this.scheduleConfiguration.getMonthBefore());
    }
}
