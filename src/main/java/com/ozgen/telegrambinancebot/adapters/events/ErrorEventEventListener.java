package com.ozgen.telegrambinancebot.adapters.events;

import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.service.telegram.TelegramErrorNotifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "bot.telegram.error.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class ErrorEventEventListener implements ApplicationListener<ErrorEvent> {

    private final TelegramErrorNotifier telegramErrorNotifier;

    @Override
    public void onApplicationEvent(ErrorEvent event) {
        log.info("new ErrorEvent consumed. event : {}", event);
        this.telegramErrorNotifier.processErrorEvent(event);
    }
}
