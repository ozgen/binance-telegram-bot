package com.ozgen.telegrambinancebot.repository;

import com.ozgen.telegrambinancebot.model.bot.SellOrder;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellOrderRepository extends JpaRepository<SellOrder, String> {

    Optional<SellOrder> findByTradingSignal(TradingSignal tradingSignal);
    List<SellOrder> findByTradingSignalIn(List<TradingSignal> tradingSignals);


}
