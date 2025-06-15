package com.ozgen.telegrambinancebot.adapters.events;

import com.ozgen.telegrambinancebot.manager.binance.BinanceProgressiveChunkedBuyOrderManager;
import com.ozgen.telegrambinancebot.model.events.NewProgressiveChunkedBuyOrderEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class NewProgressiveChunkedBuyOrderEventListenerTest {

    private BinanceProgressiveChunkedBuyOrderManager manager;
    private NewProgressiveChunkedBuyOrderEventListener listener;

    @BeforeEach
    void setUp() {
        manager = Mockito.mock(BinanceProgressiveChunkedBuyOrderManager.class);
        listener = new NewProgressiveChunkedBuyOrderEventListener(manager);
    }

    @Test
    void shouldDelegateEventToManager() {
        // given
        NewProgressiveChunkedBuyOrderEvent event = Mockito.mock(NewProgressiveChunkedBuyOrderEvent.class);

        // when
        listener.onApplicationEvent(event);

        // then
        Mockito.verify(manager).handleProgressiveChunkedBuy(event);
    }
}
