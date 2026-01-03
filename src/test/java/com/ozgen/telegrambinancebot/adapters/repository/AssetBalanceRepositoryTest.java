package com.ozgen.telegrambinancebot.adapters.repository;

import com.ozgen.telegrambinancebot.model.binance.AssetBalance;
import com.ozgen.telegrambinancebot.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@DataJpaTest
@ActiveProfiles("test")
@EnableJpaRepositories(basePackages = "com.ozgen.telegrambinancebot.adapters.repository")
public class AssetBalanceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AssetBalanceRepository assetBalanceRepository;

    @BeforeEach
    public void setUp() throws Exception {
        List<AssetBalance> assets = TestData.getAssets();
        assets.forEach(assetBalance -> this.entityManager.persist(assetBalance));
    }

    @Test
    public void testFindAll() {
        List<AssetBalance> assetBalances = this.assetBalanceRepository.findAll();

        assertFalse(assetBalances.isEmpty());
        assertEquals(7, assetBalances.size());
    }
}
