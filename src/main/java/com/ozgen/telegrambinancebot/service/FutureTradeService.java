package com.ozgen.telegrambinancebot.service;

import com.ozgen.telegrambinancebot.repository.FutureTradeRepository;
import com.ozgen.telegrambinancebot.model.bot.FutureTrade;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FutureTradeService {

    private final FutureTradeRepository futureTradeRepository;


    public FutureTradeService(FutureTradeRepository futureTradeRepository) {
        this.futureTradeRepository = futureTradeRepository;
    }

    public FutureTrade createFutureTrade(FutureTrade futureTrade) {
        return this.futureTradeRepository.save(futureTrade);
    }

    public FutureTrade findOne(UUID id) {
        return this.futureTradeRepository.findById(id).orElse(null);
    }

}
