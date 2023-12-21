package com.ozgen.telegrambinancebot.manager.bot;

import com.ozgen.telegrambinancebot.configuration.properties.ScheduleConfiguration;
import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.model.events.IncomingTradingSignalEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import com.ozgen.telegrambinancebot.utils.DateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class TradeSignalManager {
    private static final Logger log = LoggerFactory.getLogger(TradeSignalManager.class);

    private final ApplicationEventPublisher publisher;
    private final TradingSignalService tradingSignalService;
    private final ScheduleConfiguration scheduleConfiguration;

    public TradeSignalManager(ApplicationEventPublisher publisher, TradingSignalService tradingSignalService, ScheduleConfiguration scheduleConfiguration) {
        this.publisher = publisher;
        this.tradingSignalService = tradingSignalService;
        this.scheduleConfiguration = scheduleConfiguration;
    }

    public void processInitTradingSignals() {
        List<Integer> list = List.of(ProcessStatus.INIT);
        Date dateBeforeInMonths = DateFactory.getDateBeforeInMonths(this.scheduleConfiguration.getMonthBefore());
        List<TradingSignal> tradingSignals = this.tradingSignalService.getTradingSignalsAfterDateAndIsProcessIn(dateBeforeInMonths, list);
        tradingSignals.forEach(this::processTradingSignal);
    }

    private void processTradingSignal(TradingSignal tradingSignal) {
        IncomingTradingSignalEvent incomingTradingSignalEvent = new IncomingTradingSignalEvent(this, tradingSignal);
        this.publisher.publishEvent(incomingTradingSignalEvent);
    }
}
