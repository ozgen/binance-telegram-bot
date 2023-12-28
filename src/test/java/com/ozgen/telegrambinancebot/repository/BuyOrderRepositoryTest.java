package com.ozgen.telegrambinancebot.repository;

import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
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
public class BuyOrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BuyOrderRepository buyOrderRepository;

    private TradingSignal tradingSignal;
    private BuyOrder buyOrder;

    @BeforeEach
    public void setUp() {
        this.tradingSignal = TestData.getTradingSignal();
        this.tradingSignal = this.entityManager.persist(this.tradingSignal);

        this.buyOrder = new BuyOrder();
        this.tradingSignal.setIsProcessed(ProcessStatus.SELL);
        this.buyOrder.setTradingSignal(this.tradingSignal);
        this.buyOrder.setTimes(1);
        this.buyOrder.setBuyPrice(100d);
        this.buyOrder.setCoinAmount(20.0);
        this.buyOrder = this.entityManager.persist(this.buyOrder);
    }

    @Test
    public void testFindByTradingSignal() {
        Optional<BuyOrder> foundOrder = this.buyOrderRepository.findByTradingSignal(this.tradingSignal);
        assertTrue(foundOrder.isPresent());
        assertEquals(this.buyOrder, foundOrder.get());
        BuyOrder buyOrderDb = foundOrder.get();
        assertEquals(ProcessStatus.SELL, buyOrderDb.getTradingSignal().getIsProcessed());

    }

    @Test
    public void testFindByTradingSignalIn() {
        List<BuyOrder> orders = this.buyOrderRepository.findByTradingSignalIn(List.of(this.tradingSignal));
        assertFalse(orders.isEmpty());
        assertEquals(1, orders.size());
        assertEquals(this.buyOrder, orders.get(0));
    }

    @Test
    public void testFindByTradingSignalIn_withEmptyList() {
        List<BuyOrder> orders = this.buyOrderRepository.findByTradingSignalIn(List.of());
        assertTrue(orders.isEmpty());
    }
}

