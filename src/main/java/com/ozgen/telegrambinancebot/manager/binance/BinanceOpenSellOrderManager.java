package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.ScheduleConfiguration;
import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import com.ozgen.telegrambinancebot.utils.DateFactory;
import com.ozgen.telegrambinancebot.utils.SyncUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BinanceOpenSellOrderManager {

    private static final Logger log = LoggerFactory.getLogger(BinanceOpenSellOrderManager.class);

    private final TradingSignalService tradingSignalService;
    private final BotOrderService botOrderService;
    private final ApplicationEventPublisher publisher;
    private final ScheduleConfiguration scheduleConfiguration;

    public void processOpenSellOrders() {
        Date searchDate = getSearchDate();
        List<TradingSignal> tradingSignals = this.tradingSignalService.getTradingSignalsAfterDateAndIsProcessIn(searchDate, List.of(ProcessStatus.SELL));
        if (tradingSignals.isEmpty()){
            log.info("No trading signal has been detected.");
            return;
        }
        List<BuyOrder> buyOrders = this.botOrderService.getBuyOrders(tradingSignals);

        buyOrders.forEach(this::safelyPublishNewSellOrder);
    }

    private void safelyPublishNewSellOrder(BuyOrder buyOrder) {
        try {
            this.publishNewSellOrder(buyOrder);
            SyncUtil.pauseBetweenOperations();
        } catch (Exception e) {
            log.error("Error while processing sell order for BuyOrder ID {}: {}", buyOrder.getId(), e.getMessage(), e);
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
}
