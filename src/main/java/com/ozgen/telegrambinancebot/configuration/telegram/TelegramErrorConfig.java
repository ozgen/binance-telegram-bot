package com.ozgen.telegrambinancebot.configuration.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramErrorConfig {

    private final String botUserName = "ozgenErrorBot";

    @Value("${bot.telegram.error.token}")
    private String telegramApiToken;

    public String getBotUserName() {
        return botUserName;
    }

    public String getTelegramApiToken() {
        return telegramApiToken;
    }
}
