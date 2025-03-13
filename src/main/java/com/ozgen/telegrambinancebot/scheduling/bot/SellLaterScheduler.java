package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.binance.BinanceSellLaterManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SellLaterScheduler {

    private static final Logger log = LoggerFactory.getLogger(TradingSignalScheduler.class);

    private final BinanceSellLaterManager binanceSellLaterManager;

    @Scheduled(fixedRateString = "#{${app.bot.schedule.sellLater}}")
    public void processSellLaterOrders() {
        log.info("SellLaterScheduler has been started");
        this.binanceSellLaterManager.processSellLaterOrders();
        log.info("SellLaterScheduler has been finished");
    }
}
