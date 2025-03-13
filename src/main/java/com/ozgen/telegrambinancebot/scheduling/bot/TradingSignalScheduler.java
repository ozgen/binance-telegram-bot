package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.bot.TradeSignalManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradingSignalScheduler {

    private static final Logger log = LoggerFactory.getLogger(TradingSignalScheduler.class);

    private final TradeSignalManager tradeSignalManager;

    @Scheduled(fixedRateString = "#{${app.bot.schedule.tradingSignal}}")
    public void processInitTradingSignals() {
        log.info("TradingSignalScheduler has been started");
        this.tradeSignalManager.processInitTradingSignals();
        log.info("TradingSignalScheduler has been finished");
    }
}
