package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.binance.BinanceChunkSellOrderManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class SellErrorChunkOrdersSchedulerTest {

    @Mock
    private BinanceChunkSellOrderManager binanceChunkSellOrderManager;

    @InjectMocks
    private SellErrorChunkOrdersScheduler scheduler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessSellErrorFutureTrades_TriggersProcessing() {
        // Act
        scheduler.processSellErrorFutureTrades();

        // Assert
        verify(binanceChunkSellOrderManager, times(1)).processSellChunkOrders();
    }
}
