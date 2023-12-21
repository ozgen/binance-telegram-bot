package com.ozgen.telegrambinancebot.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.bot.schedule")
public class ScheduleConfiguration {

    private int monthBefore;

    public int getMonthBefore() {
        return monthBefore;
    }

    public void setMonthBefore(int monthBefore) {
        this.monthBefore = monthBefore;
    }
}
