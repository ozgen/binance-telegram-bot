package com.ozgen.telegrambinancebot.scheduling.binance;

import com.ozgen.telegrambinancebot.manager.binance.BinanceOpenBuyOrderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BinanceOpenBuyOrderScheduler {
    private static final Logger log = LoggerFactory.getLogger(BinanceOpenBuyOrderScheduler.class);

    private final BinanceOpenBuyOrderManager binanceOpenBuyOrderManager;

    public BinanceOpenBuyOrderScheduler(BinanceOpenBuyOrderManager binanceOpenBuyOrderManager) {
        this.binanceOpenBuyOrderManager = binanceOpenBuyOrderManager;
    }

    @Scheduled(fixedRateString = "#{${app.bot.schedule.openBuyOrder}}")
    public void processOpenBuyOrders() {
        this.binanceOpenBuyOrderManager.processOpenBuyOrders();
    }
}
