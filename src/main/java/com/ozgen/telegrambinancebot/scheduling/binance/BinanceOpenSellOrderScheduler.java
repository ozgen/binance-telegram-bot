package com.ozgen.telegrambinancebot.scheduling.binance;

import com.ozgen.telegrambinancebot.manager.binance.BinanceOpenSellOrderManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinanceOpenSellOrderScheduler {
    private static final Logger log = LoggerFactory.getLogger(BinanceOpenSellOrderScheduler.class);

    private final BinanceOpenSellOrderManager binanceOpenSellOrderManager;

    @Scheduled(fixedRateString = "#{${app.bot.schedule.openSellOrder}}")
    public void processOpenSellOrders() {
        log.info("OpenSellOrderScheduler has been started");
        this.binanceOpenSellOrderManager.processOpenSellOrders();
        log.info("OpenSellOrderScheduler has been finished");
    }
}
