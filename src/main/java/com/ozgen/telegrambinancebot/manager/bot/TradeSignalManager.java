package com.ozgen.telegrambinancebot.manager.bot;

import com.ozgen.telegrambinancebot.configuration.properties.ScheduleConfiguration;
import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.model.events.IncomingTradingSignalEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import com.ozgen.telegrambinancebot.utils.DateFactory;
import com.ozgen.telegrambinancebot.utils.SyncUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TradeSignalManager {

    private final ApplicationEventPublisher publisher;
    private final TradingSignalService tradingSignalService;
    private final ScheduleConfiguration scheduleConfiguration;

    public void processInitTradingSignals() {
        log.info("Processing initial trading signals...");
        List<Integer> list = List.of(ProcessStatus.INIT);
        Date dateBeforeInMonths = DateFactory.getDateBeforeInMonths(this.scheduleConfiguration.getMonthBefore());
        log.debug("Retrieving trading signals after date: {}", dateBeforeInMonths);
        List<TradingSignal> tradingSignals = this.tradingSignalService.getTradingSignalsAfterDateAndIsProcessIn(dateBeforeInMonths, list);
        log.info("Found {} trading signals to process.", tradingSignals.size());
        tradingSignals.forEach(this::processTradingSignal);
    }

    void processTradingSignal(TradingSignal tradingSignal) {
        log.info("Processing trading signal: {}", tradingSignal.getId());
        IncomingTradingSignalEvent incomingTradingSignalEvent = new IncomingTradingSignalEvent(this, tradingSignal);
        this.publisher.publishEvent(incomingTradingSignalEvent);
        log.info("Published IncomingTradingSignalEvent for Trading Signal ID: {}", tradingSignal.getId());
        SyncUtil.pauseBetweenOperations();
    }
}
