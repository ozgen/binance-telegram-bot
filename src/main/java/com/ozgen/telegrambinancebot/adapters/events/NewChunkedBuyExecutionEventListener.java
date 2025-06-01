package com.ozgen.telegrambinancebot.adapters.events;

import com.ozgen.telegrambinancebot.manager.binance.BinanceChunkBuyOrderManager;
import com.ozgen.telegrambinancebot.model.events.NewChunkedBuyExecutionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewChunkedBuyExecutionEventListener implements ApplicationListener<NewChunkedBuyExecutionEvent> {


    private final BinanceChunkBuyOrderManager binanceChunkBuyOrderManager;

    @Override
    public void onApplicationEvent(NewChunkedBuyExecutionEvent event) {
        log.info("new NewChunkedBuyExecutionEvent consumed. event : {}", event);
        this.binanceChunkBuyOrderManager.handleChunkedBuyEvent(event);
    }
}
