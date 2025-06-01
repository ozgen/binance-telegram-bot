package com.ozgen.telegrambinancebot.scheduling.binance;

import com.ozgen.telegrambinancebot.manager.binance.BinanceChunkSellOrderManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinanceChunkOrderRetryToSellScheduler {
    private static final Logger log = LoggerFactory.getLogger(BinanceChunkOrderRetryToSellScheduler.class);

    private final BinanceChunkSellOrderManager binanceChunkSellOrderManager;

    @Scheduled(fixedRateString = "#{${app.bot.schedule.openSellOrder}}")
    public void processOpenSellOrders() {
        log.info("BinanceChunkOrderRetryToSellScheduler has been started");
        this.binanceChunkSellOrderManager.retryPendingChunks();
        log.info("BinanceChunkOrderRetryToSellScheduler has been finished");
    }
}
