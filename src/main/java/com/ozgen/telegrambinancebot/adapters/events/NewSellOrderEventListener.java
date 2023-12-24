package com.ozgen.telegrambinancebot.adapters.events;

import com.ozgen.telegrambinancebot.manager.binance.BinanceSellOrderManager;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewSellOrderEventListener implements ApplicationListener<NewSellOrderEvent> {


    private static final Logger log = LoggerFactory.getLogger(NewSellOrderEventListener.class);

    private final BinanceSellOrderManager binanceSellOrderManager;

    @Override
    public void onApplicationEvent(NewSellOrderEvent event) {
        log.info("new NewSellOrderEvent consumed. event : {}", event);
        this.binanceSellOrderManager.processNewSellOrderEvent(event);
    }
}
