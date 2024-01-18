package com.ozgen.telegrambinancebot.repository;

import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderInfoRepository extends JpaRepository<OrderInfo, String> {

}
