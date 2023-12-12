package com.ozgen.telegrambinancebot.bot.repository;

import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BuyOrderRepository extends JpaRepository<BuyOrder, UUID> {

}
