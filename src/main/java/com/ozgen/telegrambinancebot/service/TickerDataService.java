package com.ozgen.telegrambinancebot.service;

import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.adapters.repository.TickerDataRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TickerDataService {
    private static final Logger log = LoggerFactory.getLogger(TickerDataService.class);

    private final TickerDataRepository tickerDataRepository;


    public TickerData createTickerData(TickerData tickerData) {
        try {
            TickerData saved = this.tickerDataRepository.save(tickerData);
            log.info("TickerData created: {}", saved);
            return saved;
        } catch (Exception e) {
            log.error("Error creating TickerData: {}", e.getMessage(), e);
            return tickerData;
        }
    }

}
