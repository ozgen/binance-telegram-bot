package com.ozgen.telegrambinancebot.adapters.repository;

import com.ozgen.telegrambinancebot.model.ExecutionStrategy;
import com.ozgen.telegrambinancebot.model.bot.ChunkOrder;
import com.ozgen.telegrambinancebot.model.bot.OrderStatus;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaRepositories(basePackages = "com.ozgen.telegrambinancebot.adapters.repository")
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
        TradingSignal tradingSignal = TestData.getTradingSignal();
        tradingSignal.setExecutionStrategy(ExecutionStrategy.CHUNKED);
        this.entityManager.persist(tradingSignal);
        this.chunkOrder.setTradingSignal(tradingSignal);

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
        List<ChunkOrder> result = this.chunkOrderRepository.findAllByCreatedAtAfterAndStatusInAndTradingSignal_ExecutionStrategy(
                beforeCreation,
                List.of(OrderStatus.BUY_EXECUTED),
                ExecutionStrategy.CHUNKED
        );
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    public void testFindAllByTradingSignalIdAndStatusIn() {
        // given
        List<OrderStatus> statuses = List.of(OrderStatus.BUY_EXECUTED);
        String tradingSignalId = chunkOrder.getTradingSignal().getId();

        // when
        List<ChunkOrder> result = chunkOrderRepository
                .findAllByTradingSignalIdAndStatusIn(tradingSignalId, statuses);

        // then
        assertFalse(result.isEmpty(), "Result should not be empty");
        assertEquals(1, result.size(), "Should find one matching ChunkOrder");
        assertEquals(OrderStatus.BUY_EXECUTED, result.getFirst().getStatus());
        assertEquals(tradingSignalId, result.getFirst().getTradingSignal().getId());
    }
}
