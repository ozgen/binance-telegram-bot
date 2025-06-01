package com.ozgen.telegrambinancebot.scheduling.binance;

import com.ozgen.telegrambinancebot.manager.binance.BinanceChunkSellOrderManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BinanceChunkOrderRetryToSellSchedulerTest {

    @Mock
    private BinanceChunkSellOrderManager binanceChunkSellOrderManager;

    @InjectMocks
    private BinanceChunkOrderRetryToSellScheduler scheduler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessOpenSellOrders_TriggersRetryPendingChunks() {
        // Act
        scheduler.processOpenSellOrders();

        // Assert
        verify(binanceChunkSellOrderManager, times(1)).retryPendingChunks();
    }
}
