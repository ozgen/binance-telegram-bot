package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.bot.FutureTradeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SellErrorFutureTradeScheduler {

    private static final Logger log = LoggerFactory.getLogger(SellErrorFutureTradeScheduler.class);

    private final FutureTradeManager futureTradeManager;

    public SellErrorFutureTradeScheduler(FutureTradeManager futureTradeManager) {
        this.futureTradeManager = futureTradeManager;
    }

    @Scheduled(fixedRateString = "#{${app.bot.schedule.sellError}}")
    public void processSellErrorFutureTrades() {
        this.futureTradeManager.processSellErrorFutureTrades();
    }
}
