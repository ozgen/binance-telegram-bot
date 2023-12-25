package com.ozgen.telegrambinancebot.repository;


import com.ozgen.telegrambinancebot.model.binance.CancelAndNewOrderResponse;
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
public class CancelAndNewOrderResponseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CancelAndNewOrderResponseRepository cancelAndNewOrderResponseRepository;

    @BeforeEach
    public void setUp() throws Exception {
        CancelAndNewOrderResponse cancelAndNewOrderResponse = TestData.getCancelAndNewOrderResponse();
        this.entityManager.persist(cancelAndNewOrderResponse);
    }

    @Test
    public void testFindAll(){
        List<CancelAndNewOrderResponse> orderResponses = this.cancelAndNewOrderResponseRepository.findAll();

        assertFalse(orderResponses.isEmpty());
        assertEquals(1, orderResponses.size());
    }
}
