package com.ozgen.telegrambinancebot.configuration.binance;

import com.binance.connector.client.impl.SpotClientImpl;
import com.ozgen.telegrambinancebot.configuration.binance.signature.CustomEd25519SignatureGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
@Profile("!test")
public class BinanceConfig {

    @Value("${binance-api-key}")
    private String apiKey;

    @Value("${binance-secret-key}")
    private String secretKey;

    @Bean
    public SpotClientImpl binanceClient() throws IOException {

        CustomEd25519SignatureGenerator signatureGenerator = new CustomEd25519SignatureGenerator(secretKey);
        return new SpotClientImpl(apiKey, signatureGenerator, "https://api.binance.com");
    }
}
