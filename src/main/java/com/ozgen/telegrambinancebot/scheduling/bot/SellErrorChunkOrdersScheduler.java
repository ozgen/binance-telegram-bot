package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.binance.BinanceChunkSellOrderManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SellErrorChunkOrdersScheduler {

    private static final Logger log = LoggerFactory.getLogger(SellErrorChunkOrdersScheduler.class);

    private final BinanceChunkSellOrderManager binanceChunkSellOrderManager;

    @Scheduled(fixedRateString = "#{${app.bot.schedule.sellError}}")
    public void processSellErrorFutureTrades() {
        log.info("SellErrorChunkOrdersScheduler has been started");
        this.binanceChunkSellOrderManager.processSellChunkOrders();
        log.info("SellErrorChunkOrdersScheduler has been finished");
    }
}
