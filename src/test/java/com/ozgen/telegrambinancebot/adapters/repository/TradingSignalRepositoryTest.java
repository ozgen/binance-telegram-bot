package com.ozgen.telegrambinancebot.adapters.repository;

import com.ozgen.telegrambinancebot.model.ExecutionStrategy;
import com.ozgen.telegrambinancebot.model.TradingStrategy;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaRepositories(basePackages = "com.ozgen.telegrambinancebot.adapters.repository")
public class TradingSignalRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TradingSignalRepository tradingSignalRepository;

    private TradingSignal signal1, signal2;

    @BeforeEach
    public void setUp() {
        signal1 = new TradingSignal();
        signal1.setSymbol("BNBBTC");
        signal1.setStopLoss("50000");
        signal1.setEntryStart("5000");
        signal1.setEntryEnd("50000");
        signal1.setCreatedAt(new Date());
        entityManager.persist(signal1);

        signal2 = new TradingSignal();
        signal2.setSymbol("ETHBTC");
        signal2.setStopLoss("4000");
        signal1.setEntryStart("5000");
        signal1.setEntryEnd("50000");
        signal2.setCreatedAt(new Date(System.currentTimeMillis() - 100000000)); // Date in the past
        entityManager.persist(signal2);
    }

    @Test
    public void testFindAllByIdIn() {
        List<TradingSignal> signals = tradingSignalRepository.findAllByIdInAndStrategyInAndExecutionStrategy(Arrays.asList(signal1.getId(), signal2.getId()), List.of(TradingStrategy.DEFAULT), ExecutionStrategy.DEFAULT);
        assertThat(signals).containsExactlyInAnyOrder(signal1, signal2);
    }

    @Test
    public void testFindAllByCreatedAtAfterAndIsProcessedIn() {
        List<TradingSignal> signals = tradingSignalRepository.findAllByCreatedAtAfterAndIsProcessedInAndStrategyInAndExecutionStrategy(new Date(System.currentTimeMillis() - 50000000), Arrays.asList(0, 1), List.of(TradingStrategy.DEFAULT), ExecutionStrategy.DEFAULT); // Assuming 0 and 1 are valid process statuses
        assertThat(signals).contains(signal1);
    }
}

