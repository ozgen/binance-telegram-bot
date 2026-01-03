package com.ozgen.telegrambinancebot.adapters.repository;

import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
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
public class OrderInfoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderInfoRepository orderInfoRepository;

    @BeforeEach
    public void setUp() throws Exception {
        List<OrderInfo> orderInfos = TestData.getOrderInfos();
        for (OrderInfo order : orderInfos) {
            this.entityManager.persist(order);
        }
    }

    @Test
    public void testFindAll() {
        List<OrderInfo> orderInfos = this.orderInfoRepository.findAll();

        assertFalse(orderInfos.isEmpty());
        assertEquals(1, orderInfos.size());
    }
}
