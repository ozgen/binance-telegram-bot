package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.binance.BinanceProgressiveChunkedBuyOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProgressiveChunkBuyOrdersScheduler {

    private final BinanceProgressiveChunkedBuyOrderManager binanceProgressiveChunkedBuyOrderManager;

    @Scheduled(fixedRateString = "#{${app.bot.schedule.sellLater}}")
    public void processSellLaterOrders() {
        log.info("ProgressiveChunkBuyOrdersScheduler has been started");
        this.binanceProgressiveChunkedBuyOrderManager.processExecutedProgressiveBuyChunks();
        log.info("ProgressiveChunkBuyOrdersScheduler has been finished");
    }
}
