package com.ozgen.telegrambinancebot.adapters.events;

import com.ozgen.telegrambinancebot.manager.binance.BinanceProgressiveChunkedSellOrderManager;
import com.ozgen.telegrambinancebot.model.events.NewProgressiveChunkedSellOrderEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class NewProgressiveChunkSellOrderEventListenerTest {

    private BinanceProgressiveChunkedSellOrderManager manager;
    private NewProgressiveChunkSellOrderEventListener listener;

    @BeforeEach
    void setUp() {
        manager = Mockito.mock(BinanceProgressiveChunkedSellOrderManager.class);
        listener = new NewProgressiveChunkSellOrderEventListener(manager);
    }

    @Test
    void shouldDelegateEventToManager() {
        // given
        NewProgressiveChunkedSellOrderEvent event = Mockito.mock(NewProgressiveChunkedSellOrderEvent.class);

        // when
        listener.onApplicationEvent(event);

        // then
        Mockito.verify(manager).onNewSellProgressiveChunkOrderEvent(event);
    }
}
