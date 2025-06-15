package com.ozgen.telegrambinancebot.scheduling.binance;

import com.ozgen.telegrambinancebot.manager.binance.BinanceProgressiveChunkedSellOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceProgressiveChunkOrderRetryToSellScheduler {

    private final BinanceProgressiveChunkedSellOrderManager binanceProgressiveChunkedSellOrderManager;

    @Scheduled(fixedRateString = "#{${app.bot.schedule.openSellOrder}}")
    public void retryPendingChunks() {
        log.info("BinanceProgressiveChunkOrderRetryToSellScheduler has been started");
        this.binanceProgressiveChunkedSellOrderManager.retryPendingChunks();
        log.info("BinanceProgressiveChunkOrderRetryToSellScheduler has been finished");
    }
}
