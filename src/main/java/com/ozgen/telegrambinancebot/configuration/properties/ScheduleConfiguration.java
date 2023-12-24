package com.ozgen.telegrambinancebot.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.bot.schedule")
@Getter
@Setter
public class ScheduleConfiguration {

    private int monthBefore;
}
