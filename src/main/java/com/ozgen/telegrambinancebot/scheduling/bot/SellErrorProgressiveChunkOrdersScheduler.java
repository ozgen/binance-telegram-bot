package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.binance.BinanceProgressiveChunkedSellOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SellErrorProgressiveChunkOrdersScheduler {

    private final BinanceProgressiveChunkedSellOrderManager binanceProgressiveChunkedSellOrderManager;

    @Scheduled(fixedRateString = "#{${app.bot.schedule.sellError}}")
    public void processSellChunkOrders() {
        log.info("SellErrorProgressiveChunkOrdersScheduler has been started");
        this.binanceProgressiveChunkedSellOrderManager.processSellChunkOrders();
        log.info("SellErrorProgressiveChunkOrdersScheduler has been finished");
    }
}
