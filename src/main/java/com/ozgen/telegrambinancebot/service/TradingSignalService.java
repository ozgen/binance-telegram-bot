package com.ozgen.telegrambinancebot.service;

import com.ozgen.telegrambinancebot.model.TradingStrategy;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.repository.TradingSignalRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradingSignalService {

    private static final Logger log = LoggerFactory.getLogger(TradingSignalService.class);


    private final TradingSignalRepository repository;

    public TradingSignal saveTradingSignal(TradingSignal tradingSignal) {
        try {
            TradingSignal saved = this.repository.save(tradingSignal);
            log.info("TradingSignal created: {}", saved);
            return saved;
        } catch (Exception e) {
            log.error("Error creating TradingSignal: {}", e.getMessage(), e);
            return tradingSignal;
        }
    }

    public List<TradingSignal> getTradingSignalsByIdList(List<String> uuidList) {
        return this.repository.findAllByIdInAndStrategyIn(uuidList, List.of(TradingStrategy.DEFAULT));
    }

    public List<TradingSignal> getDefaultTradingSignalsAfterDateAndIsProcessIn(Date date, List<Integer> processStatuses) {
        return this.repository.findAllByCreatedAtAfterAndIsProcessedInAndStrategyIn(date, processStatuses, List.of(TradingStrategy.DEFAULT));
    }

    public List<TradingSignal> getSellLaterTradingSignalsAfterDateAndIsProcessIn(Date date, List<Integer> processStatuses) {
        return this.repository.findAllByCreatedAtAfterAndIsProcessedInAndStrategyIn(date, processStatuses, List.of(TradingStrategy.SELL_LATER));
    }

    public List<TradingSignal> getAllTradingSignalsAfterDateAndIsProcessIn(Date date, List<Integer> processStatuses) {
        return this.repository.findAllByCreatedAtAfterAndIsProcessedInAndStrategyIn(date, processStatuses, List.of(TradingStrategy.DEFAULT, TradingStrategy.SELL_LATER));
    }
}
