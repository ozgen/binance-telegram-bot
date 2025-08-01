package com.ozgen.telegrambinancebot.adapters.events;

import com.ozgen.telegrambinancebot.manager.binance.BinanceChunkBuyOrderManager;
import com.ozgen.telegrambinancebot.model.events.NewChunkedBuyOrderEvent;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewChunkedBuyOrderEventListener implements ApplicationListener<NewChunkedBuyOrderEvent> {

    private final BinanceChunkBuyOrderManager binanceChunkBuyOrderManager;

    @Override
    public void onApplicationEvent(@NotNull NewChunkedBuyOrderEvent event) {
        log.info("new NewChunkedBuyOrderEvent consumed. event : {}", event);
        this.binanceChunkBuyOrderManager.handleChunkedBuyEvent(event);
    }
}
