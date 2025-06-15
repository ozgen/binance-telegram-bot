package com.ozgen.telegrambinancebot.service;

import com.ozgen.telegrambinancebot.adapters.repository.TradingSignalRepository;
import com.ozgen.telegrambinancebot.model.ExecutionStrategy;
import com.ozgen.telegrambinancebot.model.TradingStrategy;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradingSignalService {

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

    public void updateTradingSignal(TradingSignal updatedSignal) {
        try {
            if (updatedSignal.getId() == null || !repository.existsById(updatedSignal.getId())) {
                log.warn("TradingSignal with ID {} does not exist. Skipping update.", updatedSignal.getId());
                return;
            }
            TradingSignal saved = repository.save(updatedSignal);
            log.info("TradingSignal updated: {}", saved);
        } catch (Exception e) {
            log.error("Error updating TradingSignal: {}", e.getMessage(), e);
        }
    }

    public List<TradingSignal> getTradingSignalsByIdList(List<String> uuidList) {
        return this.repository.findAllByIdInAndStrategyInAndExecutionStrategy(uuidList, List.of(TradingStrategy.DEFAULT), ExecutionStrategy.DEFAULT);
    }

    public List<TradingSignal> getDefaultTradingSignalsAfterDateAndIsProcessIn(Date date, List<Integer> processStatuses) {
        return this.repository.findAllByCreatedAtAfterAndIsProcessedInAndStrategyInAndExecutionStrategy(date, processStatuses, List.of(TradingStrategy.DEFAULT), ExecutionStrategy.DEFAULT);
    }

    public List<TradingSignal> getSellLaterTradingSignalsAfterDateAndIsProcessIn(Date date, List<Integer> processStatuses) {
        return this.repository.findAllByCreatedAtAfterAndIsProcessedInAndStrategyInAndExecutionStrategy(date, processStatuses, List.of(TradingStrategy.SELL_LATER), ExecutionStrategy.DEFAULT);
    }

    public List<TradingSignal> getAllTradingSignalsAfterDateAndIsProcessIn(Date date, List<Integer> processStatuses) {
        return this.repository.findAllByCreatedAtAfterAndIsProcessedInAndStrategyInAndExecutionStrategy(date, processStatuses, List.of(TradingStrategy.DEFAULT, TradingStrategy.SELL_LATER), ExecutionStrategy.DEFAULT);
    }

    public List<TradingSignal> getAllTradingSignalsAfterDateAndIsProcessInForChunkOrders(Date date, List<Integer> processStatuses) {
        return this.repository.findAllByCreatedAtAfterAndIsProcessedInAndStrategyInAndExecutionStrategy(date, processStatuses, List.of(TradingStrategy.DEFAULT, TradingStrategy.SELL_LATER), ExecutionStrategy.CHUNKED);
    }

    public List<TradingSignal> getAllTradingSignalsAfterDateAndIsProcessInForPrChunkOrders(Date date, List<Integer> processStatuses) {
        return this.repository.findAllByCreatedAtAfterAndIsProcessedInAndStrategyInAndExecutionStrategy(date, processStatuses, List.of(TradingStrategy.DEFAULT, TradingStrategy.SELL_LATER), ExecutionStrategy.CHUNKED_PROGRESSIVE);
    }
}
