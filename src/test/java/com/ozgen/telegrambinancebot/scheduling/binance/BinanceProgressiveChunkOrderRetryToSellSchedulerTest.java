package com.ozgen.telegrambinancebot.scheduling.binance;

import com.ozgen.telegrambinancebot.manager.binance.BinanceProgressiveChunkedSellOrderManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BinanceProgressiveChunkOrderRetryToSellSchedulerTest {

    private BinanceProgressiveChunkedSellOrderManager sellOrderManager;
    private BinanceProgressiveChunkOrderRetryToSellScheduler scheduler;

    @BeforeEach
    void setUp() {
        sellOrderManager = Mockito.mock(BinanceProgressiveChunkedSellOrderManager.class);
        scheduler = new BinanceProgressiveChunkOrderRetryToSellScheduler(sellOrderManager);
    }

    @Test
    void shouldRetryPendingChunksWhenScheduled() {
        // when
        scheduler.retryPendingChunks();

        // then
        Mockito.verify(sellOrderManager).retryPendingChunks();
    }
}
