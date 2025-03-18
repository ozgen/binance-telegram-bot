package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.ScheduleConfiguration;
import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.bot.SellOrder;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import com.ozgen.telegrambinancebot.utils.DateFactory;
import com.ozgen.telegrambinancebot.utils.SyncUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceOpenSellOrderManager {

    private final TradingSignalService tradingSignalService;
    private final BotOrderService botOrderService;
    private final ApplicationEventPublisher publisher;
    private final ScheduleConfiguration scheduleConfiguration;

    public void processOpenSellOrders() {
        Date searchDate = getSearchDate();
        List<TradingSignal> tradingSignals = this.tradingSignalService
                .getDefaultTradingSignalsAfterDateAndIsProcessIn(searchDate, List.of(ProcessStatus.BUY));
        if (tradingSignals.isEmpty()) {
            log.info("No trading signal has been detected.");
            return;
        }

        List<BuyOrder> buyOrders = this.botOrderService.getBuyOrders(tradingSignals);

        buyOrders.forEach(this::safelyPublishNewSellOrder);
    }

    public void processNotCompletedSellOrders() {
        Date searchDate = getSearchDate();
        List<TradingSignal> sellSignals = this.tradingSignalService
                .getDefaultTradingSignalsAfterDateAndIsProcessIn(searchDate, List.of(ProcessStatus.SELL));

        if (sellSignals.isEmpty()) {
            log.info("No trading signal has been detected.");
            return;
        }
        List<SellOrder> sellOrders = this.botOrderService.getSellOrders(sellSignals);
        List<BuyOrder> buyOrders = this.botOrderService.getBuyOrders(sellSignals);
        List<BuyOrder> matchingOrders = this.findMatchingOrders(sellOrders, buyOrders);
        if (matchingOrders.isEmpty()) {
            log.info("No matching has been detected.");
            return;
        }
        matchingOrders.forEach(this::safelyPublishNewSellOrder);
    }

    private List<BuyOrder> findMatchingOrders(List<SellOrder> sellOrders, List<BuyOrder> buyOrders) {
        return buyOrders.stream()
                .filter(buyOrder -> {
                    TradingSignal buySignal = buyOrder.getTradingSignal();
                    return sellOrders.stream()
                            .anyMatch(sellOrder -> sellOrder.getTradingSignal().getId().equals(buySignal.getId()) &&
                                    sellOrder.getCoinAmount() < buyOrder.getTotalCoinAmount());
                })
                .collect(Collectors.toList());
    }

    private void safelyPublishNewSellOrder(BuyOrder buyOrder) {
        try {
            this.publishNewSellOrder(buyOrder);
            SyncUtil.pauseBetweenOperations();
        } catch (Exception e) {
            log.error("Error while processing sell order for BuyOrder ID {}: {}", buyOrder.getId(), e.getMessage(), e);
            this.processException(e);
        }
    }

    private void publishNewSellOrder(BuyOrder buyOrder) {
        NewSellOrderEvent newSellOrderEvent = new NewSellOrderEvent(this, buyOrder);
        this.publisher.publishEvent(newSellOrderEvent);
        log.info("Published NewSellOrderEvent for BuyOrder {}", buyOrder.getId());
    }

    private Date getSearchDate() {
        return DateFactory.getDateBeforeInMonths(this.scheduleConfiguration.getMonthBefore());
    }

    private void processException(Exception e) {
        ErrorEvent errorEvent = new ErrorEvent(this, e);
        this.publisher.publishEvent(errorEvent);
    }
}
