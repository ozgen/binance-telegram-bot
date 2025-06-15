package com.ozgen.telegrambinancebot.scheduling.binance;

import com.ozgen.telegrambinancebot.manager.binance.BinanceOpenProgressiveChunkBuyOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceOpenBuyProgressiveChunkOrderScheduler {

    private final BinanceOpenProgressiveChunkBuyOrderManager binanceOpenProgressiveChunkBuyOrderManager;

    @Scheduled(fixedRateString = "#{${app.bot.schedule.openBuyOrder}}")
    public void processOpenProgressiveChunks() {
        log.info("BinanceOpenBuyProgressiveChunkOrderScheduler has been started");
        this.binanceOpenProgressiveChunkBuyOrderManager.processOpenProgressiveChunks();
        log.info("BinanceOpenBuyProgressiveChunkOrderScheduler has been finished");
    }
}
