package com.ozgen.telegrambinancebot.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.bot.investment")
@Getter
@Setter
public class BotConfiguration {

    private double amount;
    private double perAmount;
    private double percentageInc;
    private double profitPercentage;
    private String currency;
    private String currencyRate;
}
