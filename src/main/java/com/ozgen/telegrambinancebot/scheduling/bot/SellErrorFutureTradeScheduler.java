package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.bot.FutureTradeManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SellErrorFutureTradeScheduler {

    private static final Logger log = LoggerFactory.getLogger(SellErrorFutureTradeScheduler.class);

    private final FutureTradeManager futureTradeManager;

    @Scheduled(fixedRateString = "#{${app.bot.schedule.sellError}}")
    public void processSellErrorFutureTrades() {
        log.info("SellErrorFutureTradeScheduler has been started");
        this.futureTradeManager.processSellErrorFutureTrades();
        log.info("SellErrorFutureTradeScheduler has been finished");
    }
}
