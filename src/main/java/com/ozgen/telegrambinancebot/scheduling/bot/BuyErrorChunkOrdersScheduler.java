package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.binance.BinanceChunkBuyOrderManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuyErrorChunkOrdersScheduler {
    private static final Logger log = LoggerFactory.getLogger(BuyErrorChunkOrdersScheduler.class);


    private final BinanceChunkBuyOrderManager binanceChunkBuyOrderManager;

    @Scheduled(fixedRateString = "#{${app.bot.schedule.buyError}}")
    public void processBuyErrorFutureTrades() {
        log.info("BuyErrorChunkOrdersScheduler has been started");
        this.binanceChunkBuyOrderManager.processFailedBuyChunkOrders();
        log.info("BuyErrorChunkOrdersScheduler has been finished");
    }
}
