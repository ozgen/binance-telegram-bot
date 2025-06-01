package com.ozgen.telegrambinancebot.adapters.repository;

import com.ozgen.telegrambinancebot.model.bot.ChunkOrder;
import com.ozgen.telegrambinancebot.model.bot.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ChunkOrderRepository extends JpaRepository<ChunkOrder, String> {

    List<ChunkOrder> findAllByStatus(OrderStatus orderStatus);
    List<ChunkOrder> findAllByCreatedAtAfterAndStatusIn(Date date, List<OrderStatus> orderStatus);
}
