package com.ozgen.telegrambinancebot.scheduling.bot;

import com.ozgen.telegrambinancebot.manager.binance.BinanceProgressiveChunkedSellOrderManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SellErrorProgressiveChunkOrdersSchedulerTest {

    private BinanceProgressiveChunkedSellOrderManager sellOrderManager;
    private SellErrorProgressiveChunkOrdersScheduler scheduler;

    @BeforeEach
    void setUp() {
        sellOrderManager = Mockito.mock(BinanceProgressiveChunkedSellOrderManager.class);
        scheduler = new SellErrorProgressiveChunkOrdersScheduler(sellOrderManager);
    }

    @Test
    void shouldProcessSellChunkOrdersWhenScheduled() {
        // when
        scheduler.processSellChunkOrders();

        // then
        Mockito.verify(sellOrderManager).processSellChunkOrders();
    }
}
