package com.ozgen.telegrambinancebot.repository;

import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TradingSignalRepository extends JpaRepository<TradingSignal, UUID> {

    TradingSignal findBySymbolAndStopLoss(String symbol, String stopLoss);
}
