package com.ozgen.telegrambinancebot.repository;

import com.ozgen.telegrambinancebot.model.bot.SellOrder;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SellOrderRepository extends JpaRepository<SellOrder, UUID> {

    Optional<SellOrder> findByTradingSignal(TradingSignal tradingSignal);

}
