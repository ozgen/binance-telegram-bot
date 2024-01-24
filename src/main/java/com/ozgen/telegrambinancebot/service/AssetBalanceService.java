package com.ozgen.telegrambinancebot.service;

import com.ozgen.telegrambinancebot.model.binance.AssetBalance;
import com.ozgen.telegrambinancebot.repository.AssetBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetBalanceService {
    private static final Logger log = LoggerFactory.getLogger(AssetBalanceService.class);

    private final AssetBalanceRepository assetBalanceRepository;


    public AssetBalance createAssetBalance(AssetBalance assetBalance) {
        try {
            AssetBalance saved = this.assetBalanceRepository.save(assetBalance);
            log.info("AssetBalance created: {}", saved);
            return saved;
        } catch (Exception e) {
            log.error("Error creating AssetBalance: {}", e.getMessage(), e);
            return assetBalance;
        }
    }

    public List<AssetBalance> createAssetBalances( List<AssetBalance> assetBalances) {
        try {
            List<AssetBalance> saved = this.assetBalanceRepository.saveAll(assetBalances);
            log.info("AssetBalances created: {}", saved.size());
            return saved;
        } catch (Exception e) {
            log.error("Error creating AssetBalance: {}", e.getMessage(), e);
            return assetBalances;
        }
    }

}
