package com.ozgen.telegrambinancebot.configuration.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramErrorConfig {

    @Value("${bot.telegram.error.bot_username}")
    private  String botUserName;

    @Value("${bot.telegram.error.token}")
    private String telegramApiToken;

    public String getBotUserName() {
        return botUserName;
    }

    public String getTelegramApiToken() {
        return telegramApiToken;
    }
}
