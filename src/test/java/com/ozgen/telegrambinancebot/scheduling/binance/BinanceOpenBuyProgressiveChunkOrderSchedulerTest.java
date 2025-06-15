package com.ozgen.telegrambinancebot.scheduling.binance;

import com.ozgen.telegrambinancebot.manager.binance.BinanceOpenProgressiveChunkBuyOrderManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BinanceOpenBuyProgressiveChunkOrderSchedulerTest {

    @Mock
    private BinanceOpenProgressiveChunkBuyOrderManager manager;

    private BinanceOpenBuyProgressiveChunkOrderScheduler scheduler;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        scheduler = new BinanceOpenBuyProgressiveChunkOrderScheduler(manager);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close(); // Ensures proper mock cleanup
    }

    @Test
    void testProcessOpenProgressiveChunks_invokesManager() {
        // When
        scheduler.processOpenProgressiveChunks();

        // Then
        verify(manager, times(1)).processOpenProgressiveChunks();
    }
}
