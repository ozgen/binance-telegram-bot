package com.ozgen.telegrambinancebot.adapters.repository;

import com.ozgen.telegrambinancebot.model.ExecutionStrategy;
import com.ozgen.telegrambinancebot.model.bot.ChunkOrder;
import com.ozgen.telegrambinancebot.model.bot.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ChunkOrderRepository extends JpaRepository<ChunkOrder, String> {

    List<ChunkOrder> findAllByStatus(OrderStatus orderStatus);
    List<ChunkOrder> findAllByCreatedAtAfterAndStatusInAndTradingSignal_ExecutionStrategy(
            Date date,
            List<OrderStatus> statuses,
            ExecutionStrategy executionStrategy
    );
    List<ChunkOrder> findAllByTradingSignalIdAndStatusIn(String tradingSignalId, List<OrderStatus> statuses);
}
