package com.ozgen.telegrambinancebot.adapters.events;

import com.ozgen.telegrambinancebot.manager.binance.BinanceSellOrderManager;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewSellOrderEventListener implements ApplicationListener<NewSellOrderEvent> {


    private final BinanceSellOrderManager binanceSellOrderManager;

    @Override
    public void onApplicationEvent(NewSellOrderEvent event) {
        log.info("new NewSellOrderEvent consumed. event : {}", event);
        this.binanceSellOrderManager.processNewSellOrderEvent(event);
    }
}
