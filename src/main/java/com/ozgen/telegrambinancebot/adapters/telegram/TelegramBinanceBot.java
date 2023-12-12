package com.ozgen.telegrambinancebot.adapters.telegram;


import com.ozgen.telegrambinancebot.bot.manager.telegram.TelegramMessageManager;
import com.ozgen.telegrambinancebot.configuration.telegram.TelegramConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;

@Component
public class TelegramBinanceBot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(TelegramBinanceBot.class);

    @Autowired
    private TelegramMessageManager  telegramMessageManager;

    private final TelegramConfig telegramConfig;

    public TelegramBinanceBot(TelegramConfig telegramConfig) {
        this.telegramConfig = telegramConfig;
    }

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
            Long chatId = channelPost.getChatId();
            String text = channelPost.getText();

            log.info("Received channel post \"{}\" from {}", text, chatId);

            //todo change test method
            String message = this.telegramMessageManager.testMessage();
            SendMessage response = new SendMessage();
            response.setChatId(chatId.toString());
            response.setText(message);
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
}
