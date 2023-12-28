package com.ozgen.telegrambinancebot.service.telegram;

import com.ozgen.telegrambinancebot.adapters.telegram.TelegramErrorBot;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@ConditionalOnProperty(name = "bot.telegram.error.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class TelegramErrorNotifier {

    private static final Logger log = LoggerFactory.getLogger(TelegramErrorNotifier.class);

    private final TelegramErrorBot telegramBot;

    public void processErrorEvent(ErrorEvent errorEvent) {
        String errorMessage = errorEvent.getException().getMessage();
        this.sendErrorMessage(errorMessage);
    }

    private void sendErrorMessage(String errorMessage) {
        if (this.telegramBot.getChannelId() == null) {
            log.error("Please write something to troubleshooting channel to set channel id.");
            return;
        }
        SendMessage message = new SendMessage();
        message.setChatId(this.telegramBot.getChannelId());
        message.setText(errorMessage);
        log.info(errorMessage);
        try {
            this.telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred while sending message to the channel: ", e);
        }
    }
}

