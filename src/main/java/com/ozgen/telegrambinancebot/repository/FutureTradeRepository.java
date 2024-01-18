package com.ozgen.telegrambinancebot.repository;

import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.model.bot.FutureTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FutureTradeRepository extends JpaRepository<FutureTrade, String> {

    List<FutureTrade> findByTradeStatus(TradeStatus tradeStatus);
    List<FutureTrade> findByTradeSignalId(String tradingSignalId);
    List<FutureTrade> findAllByTradeSignalIdIn(List<String> tradingSignalIdList);
}
