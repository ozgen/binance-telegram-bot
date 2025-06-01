package com.ozgen.telegrambinancebot.adapters.repository;

import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuyOrderRepository extends JpaRepository<BuyOrder, String> {

    Optional<BuyOrder> findByTradingSignal(TradingSignal tradingSignal);

    List<BuyOrder> findByTradingSignalIn(List<TradingSignal> tradingSignals);

}
