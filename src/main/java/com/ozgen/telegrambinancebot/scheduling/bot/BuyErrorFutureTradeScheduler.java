package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.bot.FutureTradeManager;
import com.ozgen.telegrambinancebot.model.TradeStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BuyErrorFutureTradeScheduler {

    private final FutureTradeManager futureTradeManager;

    public BuyErrorFutureTradeScheduler(FutureTradeManager futureTradeManager) {
        this.futureTradeManager = futureTradeManager;
    }

    @Scheduled(fixedRate = 300000) // 300000 milliseconds = 5 minutes todo move this application properties.
    public void processBuyErrorFutureTrades() {
        this.futureTradeManager.processFutureTrades(TradeStatus.ERROR_BUY);
    }
}
