package com.ozgen.telegrambinancebot.adapters.repository;


import com.ozgen.telegrambinancebot.adapters.repository.OpenOrderRepository;
import com.ozgen.telegrambinancebot.model.binance.OpenOrder;
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
public class OpenOrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OpenOrderRepository openOrderRepository;

    @BeforeEach
    public void setUp() throws Exception {
        List<OpenOrder> openOrders = TestData.getOpenOrders();
        for (OpenOrder order : openOrders) {
            this.entityManager.persist(order);
        }
    }

    @Test
    public void testFindAll() {
        List<OpenOrder> openOrders = this.openOrderRepository.findAll();

        assertFalse(openOrders.isEmpty());
        assertEquals(3, openOrders.size());
    }
}
