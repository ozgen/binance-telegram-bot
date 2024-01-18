package com.ozgen.telegrambinancebot.repository;

import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TradingSignalRepository extends JpaRepository<TradingSignal, String> {

    List<TradingSignal> findAllByIdIn(List<String> uuidList);

    List<TradingSignal>findAllByCreatedAtAfterAndIsProcessedIn(Date date, List<Integer> processStatuses);
}
