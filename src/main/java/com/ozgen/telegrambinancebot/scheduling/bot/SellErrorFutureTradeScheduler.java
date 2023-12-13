package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.bot.FutureTradeManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SellErrorFutureTradeScheduler {

    private final FutureTradeManager futureTradeManager;

    public SellErrorFutureTradeScheduler(FutureTradeManager futureTradeManager) {
        this.futureTradeManager = futureTradeManager;
    }

    @Scheduled(fixedRate = 300000) // 300000 milliseconds = 5 minutes
    public void processSellErrorFutureTrades() {
        this.futureTradeManager.processSellErrorFutureTrades();
    }
}
