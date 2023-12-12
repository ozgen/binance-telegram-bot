package com.ozgen.telegrambinancebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelegramBinanceBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramBinanceBotApplication.class, args);
    }

}
