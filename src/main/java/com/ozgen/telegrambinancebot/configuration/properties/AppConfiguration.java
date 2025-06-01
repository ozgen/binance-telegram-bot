package com.ozgen.telegrambinancebot.configuration.properties;

import com.ozgen.telegrambinancebot.model.ExecutionStrategy;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.bot.execution")
@Getter
@Setter
public class AppConfiguration {

    private ExecutionStrategy executionStrategy;
    private double minQuoteVolume;
}
