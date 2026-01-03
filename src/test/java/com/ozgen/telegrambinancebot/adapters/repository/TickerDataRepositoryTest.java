package com.ozgen.telegrambinancebot.adapters.repository;

import com.ozgen.telegrambinancebot.model.binance.TickerData;
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
public class TickerDataRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TickerDataRepository tickerDataRepository;

    @BeforeEach
    public void setUp() throws Exception {
        TickerData tickerData = TestData.getTickerData();
        this.entityManager.persist(tickerData);
    }

    @Test
    public void testFindAll() {
        List<TickerData> tickerDataList = this.tickerDataRepository.findAll();

        assertFalse(tickerDataList.isEmpty());
        assertEquals(1, tickerDataList.size());
    }
}
