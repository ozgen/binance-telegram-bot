package com.ozgen.telegrambinancebot.configuration;

import com.binance.connector.client.impl.SpotClientImpl;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class BinanceTestConfig {

    @Bean
    public SpotClientImpl binanceClientMock() {
        return Mockito.mock(SpotClientImpl.class);
    }
}
