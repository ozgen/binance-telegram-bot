package com.ozgen.telegrambinancebot.repository;

import com.ozgen.telegrambinancebot.model.binance.AssetBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetBalanceRepository extends JpaRepository<AssetBalance, String> {

}
