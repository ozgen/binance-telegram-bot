package com.ozgen.telegrambinancebot.scheduling.binance;

import com.ozgen.telegrambinancebot.manager.binance.BinanceOpenBuyChunkOrderManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BinanceOpenBuyChunkOrderSchedulerTest {

    @Mock
    private BinanceOpenBuyChunkOrderManager binanceOpenBuyChunkOrderManager;

    @InjectMocks
    private BinanceOpenBuyChunkOrderScheduler scheduler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessOpenBuyOrders_TriggersOpenChunkProcessing() {
        // Act
        scheduler.processOpenBuyOrders();

        // Assert
        verify(binanceOpenBuyChunkOrderManager, times(1)).processOpenChunkOrders();
    }
}
