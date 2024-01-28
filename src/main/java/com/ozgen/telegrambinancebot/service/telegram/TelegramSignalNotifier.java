package com.ozgen.telegrambinancebot.service.telegram;

import com.ozgen.telegrambinancebot.adapters.telegram.TelegramBinanceBot;
import com.ozgen.telegrambinancebot.model.events.InfoEvent;
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
public class TelegramSignalNotifier {

    private static final Logger log = LoggerFactory.getLogger(TelegramSignalNotifier.class);

    private final TelegramBinanceBot telegramBot;

    public void processInfoEvent(InfoEvent infoEvent) {
        String message = infoEvent.getMessage();
        this.sendInfoMessage(message);
    }

    private void sendInfoMessage(String errorMessage) {
        if (this.telegramBot.getChannelId() == null) {
            log.error("Please write something to info channel to set channel id.");
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

