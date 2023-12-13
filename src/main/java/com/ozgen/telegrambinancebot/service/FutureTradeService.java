package com.ozgen.telegrambinancebot.service;

import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.repository.FutureTradeRepository;
import com.ozgen.telegrambinancebot.model.bot.FutureTrade;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<FutureTrade> getAllFutureTradeByTradeStatus(TradeStatus tradeStatus) {
        return this.futureTradeRepository.findByTradeStatus(tradeStatus);
    }

    public List<FutureTrade> getAllFutureTradeByTradingSignal(UUID signalId) {
        return this.futureTradeRepository.findByTradeSignalId(signalId);
    }

    public List<FutureTrade> getAllFutureTradeByTradingSignals(List<UUID> uuidList) {
        return this.futureTradeRepository.findAllByTradeSignalIdIn(uuidList);
    }

    public void deleteFutureTrades(List<FutureTrade> futureTrades) {
        this.futureTradeRepository.deleteAll(futureTrades);
    }

    public void deleteFutureTrade(FutureTrade futureTrades) {
        this.futureTradeRepository.delete(futureTrades);
    }

}
