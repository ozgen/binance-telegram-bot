package com.ozgen.telegrambinancebot.adapters.events;

import com.ozgen.telegrambinancebot.manager.binance.BinanceTradingSignalManager;
import com.ozgen.telegrambinancebot.model.events.IncomingProgressiveChunkedTradingSignalEvent;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncomingProgressiveChunkedTradingSignalEventListener implements ApplicationListener<IncomingProgressiveChunkedTradingSignalEvent> {

    private final BinanceTradingSignalManager binanceTradingSignalManager;

    @Override
    public void onApplicationEvent(@NotNull IncomingProgressiveChunkedTradingSignalEvent event) {
        log.info("new IncomingProgressiveChunkedTradingSignalEvent consumed. event : {}", event);
        this.binanceTradingSignalManager.processIncomingProgressiveChunkedTradingSignalEvent(event);
    }
}
