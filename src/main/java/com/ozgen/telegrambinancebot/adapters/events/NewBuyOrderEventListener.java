package com.ozgen.telegrambinancebot.adapters.events;

import com.ozgen.telegrambinancebot.manager.binance.BinanceBuyOrderManager;
import com.ozgen.telegrambinancebot.model.events.NewBuyOrderEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewBuyOrderEventListener implements ApplicationListener<NewBuyOrderEvent> {

    private static final Logger log = LoggerFactory.getLogger(NewBuyOrderEventListener.class);

    private final BinanceBuyOrderManager binanceBuyOrderManager;

    @Override
    public void onApplicationEvent(NewBuyOrderEvent event) {
        log.info("new NewBuyOrderEvent consumed. event : {}", event);
        this.binanceBuyOrderManager.processNewBuyOrderEvent(event);
    }
}
