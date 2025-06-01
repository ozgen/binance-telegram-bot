package com.ozgen.telegrambinancebot.adapters.events;

import com.ozgen.telegrambinancebot.manager.binance.BinanceChunkSellOrderManager;
import com.ozgen.telegrambinancebot.model.events.NewSellChunkOrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewSellChunkOrderEventListener implements ApplicationListener<NewSellChunkOrderEvent> {


    private final BinanceChunkSellOrderManager binanceChunkSellOrderManager;

    @Override
    public void onApplicationEvent(NewSellChunkOrderEvent event) {
        log.info("new NewSellChunkOrderEvent consumed. event : {}", event);
        this.binanceChunkSellOrderManager.onNewSellChunkOrderEvent(event);
    }
}