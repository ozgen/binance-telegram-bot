package com.ozgen.telegrambinancebot.adapters.events;

import com.ozgen.telegrambinancebot.manager.binance.BinanceTradingSignalManager;
import com.ozgen.telegrambinancebot.model.events.IncomingChunkedTradingSignalEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncomingChunkedTradingSignalEventListener implements ApplicationListener<IncomingChunkedTradingSignalEvent> {

    private final BinanceTradingSignalManager binanceTradingSignalManager;

    @Override
    public void onApplicationEvent(IncomingChunkedTradingSignalEvent event) {
        log.info("new IncomingChunkedTradingSignalEvent consumed. event : {}", event);
        this.binanceTradingSignalManager.processIncomingChunkedTradingSignalEvent(event);
    }
}
