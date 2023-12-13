package com.ozgen.telegrambinancebot.repository;

import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface BuyOrderRepository extends JpaRepository<BuyOrder, UUID> {

    Optional<BuyOrder> findByTradingSignal(TradingSignal tradingSignal);

    List<BuyOrder> findByTradingSignalIn(List<TradingSignal> tradingSignals);

}
