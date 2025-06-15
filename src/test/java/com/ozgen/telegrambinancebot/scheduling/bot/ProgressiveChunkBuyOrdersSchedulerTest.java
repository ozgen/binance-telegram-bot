package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.binance.BinanceProgressiveChunkedBuyOrderManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ProgressiveChunkBuyOrdersSchedulerTest {

    private BinanceProgressiveChunkedBuyOrderManager buyOrderManager;
    private ProgressiveChunkBuyOrdersScheduler scheduler;

    @BeforeEach
    void setUp() {
        buyOrderManager = Mockito.mock(BinanceProgressiveChunkedBuyOrderManager.class);
        scheduler = new ProgressiveChunkBuyOrdersScheduler(buyOrderManager);
    }

    @Test
    void shouldProcessExecutedProgressiveBuyChunksWhenScheduled() {
        // when
        scheduler.processSellLaterOrders();

        // then
        Mockito.verify(buyOrderManager).processExecutedProgressiveBuyChunks();
    }
}
