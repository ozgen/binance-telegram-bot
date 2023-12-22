package com.ozgen.telegrambinancebot;

import com.ozgen.telegrambinancebot.configuration.BinanceTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(BinanceTestConfig.class)
class TelegramBinanceBotApplicationTests {

    @Test
    void contextLoads() {
    }

}
