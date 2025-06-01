package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.binance.BinanceChunkBuyOrderManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SellLaterChunkOrdersScheduler {

    private static final Logger log = LoggerFactory.getLogger(TradingSignalScheduler.class);

    private final BinanceChunkBuyOrderManager binanceChunkBuyOrderManager;

    @Scheduled(fixedRateString = "#{${app.bot.schedule.sellLater}}")
    public void processSellLaterOrders() {
        log.info("SellLaterChunkOrdersScheduler has been started");
        this.binanceChunkBuyOrderManager.processExecutedBuyChunkOrders();
        log.info("SellLaterChunkOrdersScheduler has been finished");
    }
}
