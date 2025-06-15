package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.binance.BinanceProgressiveChunkedBuyOrderManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BuyErrorProgressiveChunkOrdersSchedulerTest {

    private BinanceProgressiveChunkedBuyOrderManager buyOrderManager;
    private BuyErrorProgressiveChunkOrdersScheduler scheduler;

    @BeforeEach
    void setUp() {
        buyOrderManager = Mockito.mock(BinanceProgressiveChunkedBuyOrderManager.class);
        scheduler = new BuyErrorProgressiveChunkOrdersScheduler(buyOrderManager);
    }

    @Test
    void shouldProcessFailedProgressiveBuyChunksWhenScheduled() {
        // when
        scheduler.processFailedProgressiveBuyChunks();

        // then
        Mockito.verify(buyOrderManager).processFailedProgressiveBuyChunks();
    }
}
