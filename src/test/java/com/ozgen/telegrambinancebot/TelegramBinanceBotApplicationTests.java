package com.ozgen.telegrambinancebot;

import com.ozgen.telegrambinancebot.adapters.events.InfoEventEventListener;
import com.ozgen.telegrambinancebot.configuration.BinanceTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(BinanceTestConfig.class)
class TelegramBinanceBotApplicationTests {

    @MockBean
    private InfoEventEventListener infoEventEventListener;

    @Test
    void contextLoads() {
    }

}
