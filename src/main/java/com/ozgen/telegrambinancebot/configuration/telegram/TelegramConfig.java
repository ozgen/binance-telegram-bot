package com.ozgen.telegrambinancebot.configuration.telegram;

import com.ozgen.telegrambinancebot.adapters.telegram.TelegramBinanceBot;
import com.ozgen.telegrambinancebot.adapters.telegram.TelegramErrorBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Optional;

@Configuration
public class TelegramConfig {

    @Value("${bot.telegram.bot_username}")
    private String botUserName;

    @Value("${bot.telegram.token}")
    private String telegramApiToken;

    public String getBotUserName() {
        return botUserName;
    }

    public String getTelegramApiToken() {
        return telegramApiToken;
    }

    @Bean
    @org.springframework.context.annotation.Profile("!test")
    public TelegramBotsApi telegramBotsApi(TelegramBinanceBot binanceBot, Optional<TelegramErrorBot> errorBot) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(binanceBot);
        if (errorBot.isPresent()) {
            try {
                api.registerBot(errorBot.get());
            } catch (Exception e) {
                // Error bot might be disabled
            }
        }
        return api;
    }
}
