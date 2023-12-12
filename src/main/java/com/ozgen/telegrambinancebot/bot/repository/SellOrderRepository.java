package com.ozgen.telegrambinancebot.bot.repository;

import com.ozgen.telegrambinancebot.model.bot.SellOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SellOrderRepository extends JpaRepository<SellOrder, UUID> {

}
