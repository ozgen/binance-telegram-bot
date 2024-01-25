package com.ozgen.telegrambinancebot.adapters.telegram;


import com.ozgen.telegrambinancebot.configuration.telegram.TelegramErrorConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(name = "bot.telegram.error.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class TelegramErrorBot extends TelegramLongPollingBot {

    private final TelegramErrorConfig telegramConfig;

    private Long channelId = null;

    @Override
    public String getBotToken() {
        return this.telegramConfig.getTelegramApiToken();
    }

    @Override
    public String getBotUsername() {
        return this.telegramConfig.getBotUserName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasChannelPost()) {
            // Handle channel post
            Message channelPost = update.getChannelPost();
            channelId = channelPost.getChatId();
            String text = channelPost.getText();
            log.info("Received channel post \"{}\" from {}", text, channelId);
            SendMessage response = new SendMessage();
            response.setChatId(channelId);
            response.setText("Channel id has been configured successfully.");
            try {
                execute(response);
            } catch (Exception e) {
                log.error("Failed to send message: {}", e.getMessage());
            }
        }
    }

    @PostConstruct
    public void start() {
        log.info("username: {}, token: {}", telegramConfig.getBotUserName(), telegramConfig.getTelegramApiToken());
    }

    public Long getChannelId() {
        return channelId;
    }
}
