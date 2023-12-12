package com.ozgen.telegrambinancebot.configuration.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramConfig {

    private final String botUserName = "ozgenTelegramBot";

    @Value("${bot.telegram.token}")
    private String telegramApiToken;

    public String getBotUserName() {
        return botUserName;
    }

    public String getTelegramApiToken() {
        return telegramApiToken;
    }
}
