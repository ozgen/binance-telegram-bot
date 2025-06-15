package com.ozgen.telegrambinancebot.adapters.events;

import com.ozgen.telegrambinancebot.manager.binance.BinanceProgressiveChunkedSellOrderManager;
import com.ozgen.telegrambinancebot.model.events.NewProgressiveChunkedSellOrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewProgressiveChunkSellOrderEventListener implements ApplicationListener<NewProgressiveChunkedSellOrderEvent> {

    private final BinanceProgressiveChunkedSellOrderManager binanceProgressiveChunkedSellOrderManager;

    @Override
    public void onApplicationEvent(@NotNull NewProgressiveChunkedSellOrderEvent event) {
        log.info("new NewProgressiveChunkedSellOrderEvent consumed. event : {}", event);
        this.binanceProgressiveChunkedSellOrderManager.onNewSellProgressiveChunkOrderEvent(event);
    }
}