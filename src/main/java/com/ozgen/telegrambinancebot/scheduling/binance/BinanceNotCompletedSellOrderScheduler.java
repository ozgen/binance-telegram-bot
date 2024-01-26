package com.ozgen.telegrambinancebot.scheduling.binance;

import com.ozgen.telegrambinancebot.manager.binance.BinanceOpenSellOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceNotCompletedSellOrderScheduler {

    private final BinanceOpenSellOrderManager binanceOpenSellOrderManager;


    @Scheduled(fixedRateString = "#{${app.bot.schedule.openSellOrder}}")
    public void processNotCompletedSellOrders() {
        log.info("NotCompletedSellOrderScheduler has been started");
        this.binanceOpenSellOrderManager.processNotCompletedSellOrders();
        log.info("NotCompletedSellOrderScheduler has been finished");
    }
}
