package com.ozgen.telegrambinancebot.bot.service;

import com.ozgen.telegrambinancebot.bot.repository.TickerDataRepository;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TickerDataService {

    private final TickerDataRepository tickerDataRepository;


    public TickerDataService(TickerDataRepository tickerDataRepository) {
        this.tickerDataRepository = tickerDataRepository;
    }

    public TickerData createTickerData(TickerData tickerData) {
        return this.tickerDataRepository.save(tickerData);
    }

    public TickerData findOne(UUID id) {
        return tickerDataRepository.findById(id).orElse(null);
    }

}
