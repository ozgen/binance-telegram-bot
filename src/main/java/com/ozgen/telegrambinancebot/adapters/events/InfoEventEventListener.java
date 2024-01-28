package com.ozgen.telegrambinancebot.adapters.events;

import com.ozgen.telegrambinancebot.model.events.InfoEvent;
import com.ozgen.telegrambinancebot.service.telegram.TelegramSignalNotifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InfoEventEventListener implements ApplicationListener<InfoEvent> {

    private final TelegramSignalNotifier telegramSignalNotifier;

    @Override
    public void onApplicationEvent(InfoEvent event) {
        log.info("new InfoEvent consumed. event : {}", event);
        this.telegramSignalNotifier.processInfoEvent(event);
    }
}
