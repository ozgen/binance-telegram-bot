package com.ozgen.telegrambinancebot.repository;

import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.model.bot.FutureTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FutureTradeRepository extends JpaRepository<FutureTrade, UUID> {

    List<FutureTrade> findByTradeStatus(TradeStatus tradeStatus);
    List<FutureTrade> findByTradeSignalId(UUID tradingSignalId);
    List<FutureTrade> findAllByTradeSignalIdIn(List<UUID> tradingSignalIdList);
}
