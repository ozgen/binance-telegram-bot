package com.ozgen.telegrambinancebot.adapters.events;

import com.ozgen.telegrambinancebot.manager.binance.BinanceTradingSignalManager;
import com.ozgen.telegrambinancebot.model.events.IncomingChunkedTradingSignalEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class IncomingChunkedTradingSignalEventListenerTest {

    private BinanceTradingSignalManager binanceTradingSignalManager;
    private IncomingChunkedTradingSignalEventListener listener;

    @BeforeEach
    void setUp() {
        binanceTradingSignalManager = Mockito.mock(BinanceTradingSignalManager.class);
        listener = new IncomingChunkedTradingSignalEventListener(binanceTradingSignalManager);
    }

    @Test
    void shouldDelegateEventToManager() {
        // given
        IncomingChunkedTradingSignalEvent event = Mockito.mock(IncomingChunkedTradingSignalEvent.class);

        // when
        listener.onApplicationEvent(event);

        // then
        Mockito.verify(binanceTradingSignalManager).processIncomingChunkedTradingSignalEvent(event);
    }
}
