package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.bot.FutureTradeManager;
import com.ozgen.telegrambinancebot.model.TradeStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotInRangeFutureTradeScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotInRangeFutureTradeScheduler.class);

    private final FutureTradeManager futureTradeManager;


    @Scheduled(fixedRateString = "#{${app.bot.schedule.notInRange}}")
    public void processNotInRangeFutureTrades() {
        this.futureTradeManager.processFutureTrades(TradeStatus.NOT_IN_RANGE);
    }
}
