package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.bot.TradeSignalManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TradingSignalScheduler {
    private static final Logger log = LoggerFactory.getLogger(TradingSignalScheduler.class);

    private final TradeSignalManager tradeSignalManager;

    public TradingSignalScheduler(TradeSignalManager tradeSignalManager) {
        this.tradeSignalManager = tradeSignalManager;
    }

    @Scheduled(fixedRateString = "#{${app.bot.schedule.tradingSignal}}")
    public void processInitTradingSignals() {
        this.tradeSignalManager.processInitTradingSignals();
    }
}
