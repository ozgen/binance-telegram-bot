package com.ozgen.telegrambinancebot.service;

import com.ozgen.telegrambinancebot.repository.TradingSignalRepository;
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

    public TradingSignal saveTradingSignal(TradingSignal tradingSignal) {
        TradingSignal saved = this.repository.save(tradingSignal);
        return saved;
    }

    public TradingSignal findTradingSignal(UUID id) {
        return this.repository.findById(id).orElse(null);
    }
}
