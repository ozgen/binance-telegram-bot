package com.ozgen.telegrambinancebot.adapters.events;

import com.ozgen.telegrambinancebot.manager.binance.BinanceTradingSignalManager;
import com.ozgen.telegrambinancebot.model.events.IncomingTradingSignalEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IncomingTradingSignalEventListener implements ApplicationListener<IncomingTradingSignalEvent> {
    private static final Logger log = LoggerFactory.getLogger(IncomingTradingSignalEventListener.class);
    private final BinanceTradingSignalManager binanceTradingSignalManager;

    @Override
    public void onApplicationEvent(IncomingTradingSignalEvent event) {
        log.info("new IncomingTradingSignalEvent consumed. event : {}", event);
        this.binanceTradingSignalManager.processIncomingTradingSignalEvent(event);
    }
}
