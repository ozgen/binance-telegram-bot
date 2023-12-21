package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.bot.FutureTradeManager;
import com.ozgen.telegrambinancebot.model.TradeStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BuyErrorFutureTradeScheduler {
    private static final Logger log = LoggerFactory.getLogger(BuyErrorFutureTradeScheduler.class);


    private final FutureTradeManager futureTradeManager;

    public BuyErrorFutureTradeScheduler(FutureTradeManager futureTradeManager) {
        this.futureTradeManager = futureTradeManager;
    }

    @Scheduled(fixedRateString = "#{${app.bot.schedule.buyError}}")
    public void processBuyErrorFutureTrades() {
        this.futureTradeManager.processFutureTrades(TradeStatus.ERROR_BUY);
    }
}
