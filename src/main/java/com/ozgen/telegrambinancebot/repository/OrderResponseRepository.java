package com.ozgen.telegrambinancebot.repository;

import com.ozgen.telegrambinancebot.model.binance.OrderResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderResponseRepository extends JpaRepository<OrderResponse, String> {

}
