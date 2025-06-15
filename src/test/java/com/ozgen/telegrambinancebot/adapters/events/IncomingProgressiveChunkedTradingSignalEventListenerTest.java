package com.ozgen.telegrambinancebot.adapters.events;

import com.ozgen.telegrambinancebot.manager.binance.BinanceTradingSignalManager;
import com.ozgen.telegrambinancebot.model.events.IncomingProgressiveChunkedTradingSignalEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class IncomingProgressiveChunkedTradingSignalEventListenerTest {

    private BinanceTradingSignalManager binanceTradingSignalManager;
    private IncomingProgressiveChunkedTradingSignalEventListener listener;

    @BeforeEach
    void setUp() {
        binanceTradingSignalManager = Mockito.mock(BinanceTradingSignalManager.class);
        listener = new IncomingProgressiveChunkedTradingSignalEventListener(binanceTradingSignalManager);
    }

    @Test
    void shouldDelegateProgressiveEventToManager() {
        // given
        IncomingProgressiveChunkedTradingSignalEvent event = Mockito.mock(IncomingProgressiveChunkedTradingSignalEvent.class);

        // when
        listener.onApplicationEvent(event);

        // then
        Mockito.verify(binanceTradingSignalManager)
                .processIncomingProgressiveChunkedTradingSignalEvent(event);
    }
}
