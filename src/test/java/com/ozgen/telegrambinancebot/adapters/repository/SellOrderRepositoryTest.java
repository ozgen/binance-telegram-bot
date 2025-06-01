package com.ozgen.telegrambinancebot.adapters.repository;

import com.ozgen.telegrambinancebot.adapters.repository.SellOrderRepository;
import com.ozgen.telegrambinancebot.model.bot.SellOrder;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class SellOrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SellOrderRepository sellOrderRepository;

    private TradingSignal tradingSignal;
    private SellOrder sellOrder;

    @BeforeEach
    public void setUp() {
        this.tradingSignal = TestData.getTradingSignal();
        this.tradingSignal = this.entityManager.persist(this.tradingSignal);

        this.sellOrder = new SellOrder(); // Set properties and link to TradingSignal.
        this.sellOrder.setTradingSignal(this.tradingSignal);
        this.entityManager.persist(this.sellOrder);
    }

    @Test
    public void testFindByTradingSignal() {
        Optional<SellOrder> foundOrder = this.sellOrderRepository.findByTradingSignal(this.tradingSignal);
        assertTrue(foundOrder.isPresent());
        assertEquals(this.sellOrder, foundOrder.get());
    }

    @Test
    public void testFindByTradingSignalIn() {
        List<SellOrder> foundOrder = this.sellOrderRepository.findByTradingSignalIn(List.of(this.tradingSignal));
        assertFalse(foundOrder.isEmpty());
        assertEquals(1, foundOrder.size());
    }
}

