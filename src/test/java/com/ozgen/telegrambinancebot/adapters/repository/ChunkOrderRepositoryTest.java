package com.ozgen.telegrambinancebot.adapters.repository;

import com.ozgen.telegrambinancebot.model.bot.ChunkOrder;
import com.ozgen.telegrambinancebot.model.bot.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ChunkOrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChunkOrderRepository chunkOrderRepository;

    private ChunkOrder chunkOrder;

    @BeforeEach
    public void setUp() {
        this.chunkOrder = new ChunkOrder();
        this.chunkOrder.setStatus(OrderStatus.BUY_EXECUTED);
        this.chunkOrder.setCreatedAt(new Date(System.currentTimeMillis() - 1000)); // 1 second ago

        this.chunkOrder = this.entityManager.persist(this.chunkOrder);
    }

    @Test
    public void testFindAllByStatus() {
        List<ChunkOrder> result = this.chunkOrderRepository.findAllByStatus(OrderStatus.BUY_EXECUTED);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(OrderStatus.BUY_EXECUTED, result.get(0).getStatus());
    }

    @Test
    public void testFindAllByCreatedAtAfterAndStatusIn() {
        Date beforeCreation = new Date(System.currentTimeMillis() - 2000); // 2 seconds ago
        List<ChunkOrder> result = this.chunkOrderRepository.findAllByCreatedAtAfterAndStatusIn(
                beforeCreation,
                List.of(OrderStatus.BUY_EXECUTED)
        );
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}
