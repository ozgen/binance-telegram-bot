package com.ozgen.telegrambinancebot.repository;

import com.ozgen.telegrambinancebot.model.binance.CancelAndNewOrderResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CancelAndNewOrderResponseRepository extends JpaRepository<CancelAndNewOrderResponse, String> {

}
