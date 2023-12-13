package com.ozgen.telegrambinancebot.service;

import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.repository.TradingSignalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public TradingSignal findTradingSignal(String symbol, String stopLoss) {
        return this.repository.findBySymbolAndStopLoss(symbol,stopLoss);
    }

    public List<TradingSignal> getTradingSignalsByIdList(List<UUID> uuidList){
        return this.repository.findAllByIdIn(uuidList);
    }
}
