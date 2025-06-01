package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.binance.BinanceChunkBuyOrderManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class BuyErrorChunkOrdersSchedulerTest {

    @Mock
    private BinanceChunkBuyOrderManager binanceChunkBuyOrderManager;

    @InjectMocks
    private BuyErrorChunkOrdersScheduler scheduler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessBuyErrorFutureTrades_TriggersBuyErrorProcessing() {
        // Act
        scheduler.processBuyErrorFutureTrades();

        // Assert
        verify(binanceChunkBuyOrderManager, times(1)).processFailedBuyChunkOrders();
    }
}
