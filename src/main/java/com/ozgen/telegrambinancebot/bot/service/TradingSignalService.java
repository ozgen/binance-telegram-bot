package com.ozgen.telegrambinancebot.bot.service;

import com.ozgen.telegrambinancebot.bot.repository.TradingSignalRepository;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TradingSignalService {

    private static final Logger log = LoggerFactory.getLogger(TradingSignalService.class);


    private final TradingSignalRepository repository;

    public TradingSignalService(TradingSignalRepository repository) {
        this.repository = repository;
    }

    public TradingSignal createTradingSignal(TradingSignal tradingSignal) {
        TradingSignal saved = repository.save(tradingSignal);
        return saved;
    }

    public TradingSignal findTradingSignal(UUID id) {
        return repository.findById(id).orElse(null);
    }
}
