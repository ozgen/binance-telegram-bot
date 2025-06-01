package com.ozgen.telegrambinancebot.adapters.repository;

import com.ozgen.telegrambinancebot.adapters.repository.TickerDataRepository;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
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
