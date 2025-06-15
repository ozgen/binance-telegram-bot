package com.ozgen.telegrambinancebot.adapters.events;

import com.ozgen.telegrambinancebot.manager.binance.BinanceProgressiveChunkedBuyOrderManager;
import com.ozgen.telegrambinancebot.model.events.NewProgressiveChunkedBuyOrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewProgressiveChunkedBuyOrderEventListener implements ApplicationListener<NewProgressiveChunkedBuyOrderEvent> {

    private final BinanceProgressiveChunkedBuyOrderManager binanceProgressiveChunkedBuyOrderManager;

    @Override
    public void onApplicationEvent(@NotNull NewProgressiveChunkedBuyOrderEvent event) {
        log.info("new NewProgressiveChunkedBuyOrderEvent consumed. event : {}", event);
        this.binanceProgressiveChunkedBuyOrderManager.handleProgressiveChunkedBuy(event);
    }
}
