package com.ozgen.telegrambinancebot.scheduling.binance;

import com.ozgen.telegrambinancebot.manager.binance.BinanceOpenBuyChunkOrderManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinanceOpenBuyChunkOrderScheduler {
    private static final Logger log = LoggerFactory.getLogger(BinanceOpenBuyChunkOrderScheduler.class);

    private final BinanceOpenBuyChunkOrderManager binanceOpenBuyChunkOrderManager;

    @Scheduled(fixedRateString = "#{${app.bot.schedule.openBuyOrder}}")
    public void processOpenBuyOrders() {
        log.info("BinanceOpenBuyChunkOrderScheduler has been started");
        this.binanceOpenBuyChunkOrderManager.processOpenChunkOrders();
        log.info("BinanceOpenBuyChunkOrderScheduler has been finished");
    }
}
