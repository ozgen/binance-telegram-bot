package com.ozgen.telegrambinancebot.service;

import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.model.bot.FutureTrade;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.repository.FutureTradeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FutureTradeService {
    private static final Logger log = LoggerFactory.getLogger(FutureTradeService.class);


    private final FutureTradeRepository futureTradeRepository;



    public FutureTrade createFutureTrade(FutureTrade futureTrade) {
        return this.futureTradeRepository.save(futureTrade);
    }

    public FutureTrade createFutureTrade(TradingSignal tradingSignal, TradeStatus status) {
        FutureTrade futureTrade = new FutureTrade();
        futureTrade.setTradeStatus(status);
        futureTrade.setTradeSignalId(tradingSignal.getId());
        return this.createFutureTrade(futureTrade);
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
