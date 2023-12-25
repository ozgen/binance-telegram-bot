package com.ozgen.telegrambinancebot.repository;

import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
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
        signal1.setTakeProfits(List.of());
        signal1.setCreatedAt(new Date());
        entityManager.persist(signal1);

        signal2 = new TradingSignal();
        signal2.setSymbol("ETHBTC");
        signal2.setStopLoss("4000");
        signal1.setEntryStart("5000");
        signal1.setEntryEnd("50000");
        signal1.setTakeProfits(List.of("50000"));
        signal2.setCreatedAt(new Date(System.currentTimeMillis() - 100000000)); // Date in the past
        entityManager.persist(signal2);
    }

    @Test
    public void testFindAllByIdIn() {
        List<TradingSignal> signals = tradingSignalRepository.findAllByIdIn(Arrays.asList(signal1.getId(), signal2.getId()));
        assertThat(signals).containsExactlyInAnyOrder(signal1, signal2);
    }

    @Test
    public void testFindAllByCreatedAtAfterAndIsProcessedIn() {
        List<TradingSignal> signals = tradingSignalRepository.findAllByCreatedAtAfterAndIsProcessedIn(new Date(System.currentTimeMillis() - 50000000), Arrays.asList(0, 1)); // Assuming 0 and 1 are valid process statuses
        assertThat(signals).contains(signal1);
    }
}

