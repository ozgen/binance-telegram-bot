package com.ozgen.telegrambinancebot.scheduling.binance;

import com.ozgen.telegrambinancebot.manager.binance.BinanceOpenSellOrderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BinanceOpenSellOrderScheduler {
    private static final Logger log = LoggerFactory.getLogger(BinanceOpenSellOrderScheduler.class);

    private final BinanceOpenSellOrderManager binanceOpenSellOrderManager;

    public BinanceOpenSellOrderScheduler(BinanceOpenSellOrderManager binanceOpenSellOrderManager) {
        this.binanceOpenSellOrderManager = binanceOpenSellOrderManager;
    }

    @Scheduled(fixedRateString = "#{${app.bot.schedule.openSellOrder}}")
    public void processOpenSellOrders() {
        this.binanceOpenSellOrderManager.processOpenSellOrders();
    }
}
