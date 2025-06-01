package com.ozgen.telegrambinancebot.service;

import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.model.bot.FutureTrade;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.adapters.repository.FutureTradeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FutureTradeService {
    private static final Logger log = LoggerFactory.getLogger(FutureTradeService.class);


    private final FutureTradeRepository futureTradeRepository;


    public FutureTrade createFutureTrade(FutureTrade futureTrade) {
        try {
            FutureTrade saved = this.futureTradeRepository.save(futureTrade);
            log.info("FutureTrade created: {}", saved);
            return saved;
        } catch (Exception e) {
            log.error("Error creating FutureTrade: {}", e.getMessage(), e);
            throw e; // Rethrow to handle at a higher level or use specific business logic
        }
    }

    public FutureTrade createFutureTrade(TradingSignal tradingSignal, TradeStatus status) {
        if (tradingSignal.getId() == null || status == null) {
            log.error("trading signal is not saved before saving future trade, trading signal : {}", tradingSignal);
            return null;
        }
        FutureTrade futureTrade = new FutureTrade();
        futureTrade.setTradeStatus(status);
        futureTrade.setTradeSignalId(tradingSignal.getId());
        return this.createFutureTrade(futureTrade);
    }

    public List<FutureTrade> getAllFutureTradeByTradeStatus(TradeStatus tradeStatus) {
        return this.futureTradeRepository.findByTradeStatus(tradeStatus);
    }

    public List<FutureTrade> getAllFutureTradeByTradingSignals(List<String> uuidList) {
        return this.futureTradeRepository.findAllByTradeSignalIdIn(uuidList);
    }

    public void deleteFutureTrades(List<FutureTrade> futureTrades) {
        this.futureTradeRepository.deleteAll(futureTrades);
    }
}
