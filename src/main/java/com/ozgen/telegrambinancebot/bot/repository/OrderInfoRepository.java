package com.ozgen.telegrambinancebot.bot.repository;

import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderInfoRepository extends JpaRepository<OrderInfo, UUID> {

}
