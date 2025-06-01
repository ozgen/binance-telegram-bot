package com.ozgen.telegrambinancebot.adapters.repository;

import com.ozgen.telegrambinancebot.model.ExecutionStrategy;
import com.ozgen.telegrambinancebot.model.TradingStrategy;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TradingSignalRepository extends JpaRepository<TradingSignal, String> {

    List<TradingSignal> findAllByIdInAndStrategyInAndExecutionStrategy(List<String> uuidList, List<TradingStrategy> strategies, ExecutionStrategy executionStrategy);

    List<TradingSignal>findAllByCreatedAtAfterAndIsProcessedInAndStrategyInAndExecutionStrategy(Date date, List<Integer> processStatuses, List<TradingStrategy> strategies , ExecutionStrategy executionStrategy);
}
