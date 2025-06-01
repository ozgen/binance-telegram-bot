package com.ozgen.telegrambinancebot.manager.binance;


import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.IncomingChunkedTradingSignalEvent;
import com.ozgen.telegrambinancebot.model.events.IncomingTradingSignalEvent;
import com.ozgen.telegrambinancebot.model.events.NewBuyOrderEvent;
import com.ozgen.telegrambinancebot.model.events.NewChunkedBuyExecutionEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BinanceTradingSignalManagerTest {

    private static final String SYMBOL = "BNBBTC";
    private static final String LAST_PRICE = "100";
    private static final String LAST_PRICE_OUT_RANGE = "200";
    private static final String START_ENTRY = "90";
    private static final String END_ENTRY = "110";

    @Mock
    private BinanceApiManager binanceApiManager;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private BinanceTradingSignalManager binanceTradingSignalManager;

    @Mock
    private TickerData tickerData;
    @Mock
    private TradingSignal tradingSignal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessIncomingTradingSignalEvent() throws Exception {
        // Arrange
        when(this.tradingSignal.getSymbol())
                .thenReturn(SYMBOL);
        when(this.tradingSignal.getEntryStart())
                .thenReturn(START_ENTRY);
        when(this.tradingSignal.getEntryEnd())
                .thenReturn(END_ENTRY);
        when(this.tickerData.getLastPrice())
                .thenReturn(LAST_PRICE);
        IncomingTradingSignalEvent event = new IncomingTradingSignalEvent(this, this.tradingSignal);
        when(this.binanceApiManager.getTickerPrice24(SYMBOL))
                .thenReturn(this.tickerData);

        // Act
        this.binanceTradingSignalManager.processIncomingTradingSignalEvent(event);

        // Assert
        verify(this.binanceApiManager)
                .getTickerPrice24(SYMBOL);
        ArgumentCaptor<NewBuyOrderEvent> eventCaptor = ArgumentCaptor.forClass(NewBuyOrderEvent.class);
        verify(this.publisher)
                .publishEvent(eventCaptor.capture());
        NewBuyOrderEvent newBuyOrderEvent = eventCaptor.getValue();
        assertEquals(this.tickerData, newBuyOrderEvent.getTickerData());
        assertEquals(this.tradingSignal, newBuyOrderEvent.getTradingSignal());
    }

    @Test
    void testProcessIncomingTradingSignalEvent_withTickerDataException() throws Exception {
        // Arrange
        when(this.tradingSignal.getSymbol())
                .thenReturn(SYMBOL);
        when(this.tradingSignal.getEntryStart())
                .thenReturn(START_ENTRY);
        when(this.tradingSignal.getEntryEnd())
                .thenReturn(END_ENTRY);
        when(this.tickerData.getLastPrice())
                .thenReturn(LAST_PRICE);
        IncomingTradingSignalEvent event = new IncomingTradingSignalEvent(this, this.tradingSignal);

        when(this.binanceApiManager.getTickerPrice24(SYMBOL))
                .thenThrow(RuntimeException.class);

        // Act
        assertThrows(RuntimeException.class, () -> this.binanceTradingSignalManager.processIncomingTradingSignalEvent(event));

        // Assert
        verify(this.binanceApiManager)
                .getTickerPrice24(SYMBOL);
        verify(this.publisher, never())
                .publishEvent(any(NewBuyOrderEvent.class));
        verify(this.publisher)
                .publishEvent(any(ErrorEvent.class));
    }

    @Test
    void testProcessIncomingTradingSignalEvent_withNotAvailableRange() throws Exception {
        // Arrange
        when(this.tradingSignal.getSymbol())
                .thenReturn(SYMBOL);
        when(this.tradingSignal.getEntryStart())
                .thenReturn(START_ENTRY);
        when(this.tradingSignal.getEntryEnd())
                .thenReturn(END_ENTRY);
        when(this.tickerData.getLastPrice())
                .thenReturn(LAST_PRICE_OUT_RANGE);
        IncomingTradingSignalEvent event = new IncomingTradingSignalEvent(this, this.tradingSignal);
        when(this.binanceApiManager.getTickerPrice24(SYMBOL))
                .thenReturn(this.tickerData);

        // Act
        this.binanceTradingSignalManager.processIncomingTradingSignalEvent(event);

        // Assert
        verify(this.binanceApiManager)
                .getTickerPrice24(SYMBOL);
        verify(this.publisher, never())
                .publishEvent(any());
    }

    @Test
    void testProcessIncomingChunkedTradingSignalEvent_Success() throws Exception {
        // Arrange
        when(tradingSignal.getSymbol()).thenReturn(SYMBOL);
        when(tradingSignal.getEntryStart()).thenReturn(START_ENTRY);
        when(tradingSignal.getEntryEnd()).thenReturn(END_ENTRY);
        when(tickerData.getLastPrice()).thenReturn(LAST_PRICE);
        when(binanceApiManager.getTickerPrice24(SYMBOL)).thenReturn(tickerData);

        IncomingChunkedTradingSignalEvent event = new IncomingChunkedTradingSignalEvent(this, tradingSignal);

        // Act
        binanceTradingSignalManager.processIncomingChunkedTradingSignalEvent(event);

        // Assert
        verify(binanceApiManager).getTickerPrice24(SYMBOL);
        ArgumentCaptor<NewChunkedBuyExecutionEvent> eventCaptor = ArgumentCaptor.forClass(NewChunkedBuyExecutionEvent.class);
        verify(publisher).publishEvent(eventCaptor.capture());

        NewChunkedBuyExecutionEvent publishedEvent = eventCaptor.getValue();
        assertEquals(tradingSignal, publishedEvent.getTradingSignal());
        assertEquals(tickerData, publishedEvent.getTickerData());
    }

    @Test
    void testProcessIncomingChunkedTradingSignalEvent_FetchTickerFails() throws Exception {
        // Arrange
        when(tradingSignal.getSymbol()).thenReturn(SYMBOL);
        when(binanceApiManager.getTickerPrice24(SYMBOL)).thenThrow(new RuntimeException("API error"));

        IncomingChunkedTradingSignalEvent event = new IncomingChunkedTradingSignalEvent(this, tradingSignal);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> binanceTradingSignalManager.processIncomingChunkedTradingSignalEvent(event));

        verify(binanceApiManager).getTickerPrice24(SYMBOL);
        verify(publisher).publishEvent(any(ErrorEvent.class));
        verify(publisher, never()).publishEvent(isA(NewChunkedBuyExecutionEvent.class));
    }

    @Test
    void testProcessIncomingChunkedTradingSignalEvent_NotAvailableToBuy() throws Exception {
        // Arrange
        when(tradingSignal.getSymbol()).thenReturn(SYMBOL);
        when(tradingSignal.getEntryStart()).thenReturn(START_ENTRY);
        when(tradingSignal.getEntryEnd()).thenReturn(END_ENTRY);
        when(tickerData.getLastPrice()).thenReturn(LAST_PRICE_OUT_RANGE); // outside of entry range
        when(binanceApiManager.getTickerPrice24(SYMBOL)).thenReturn(tickerData);

        IncomingChunkedTradingSignalEvent event = new IncomingChunkedTradingSignalEvent(this, tradingSignal);

        // Act
        binanceTradingSignalManager.processIncomingChunkedTradingSignalEvent(event);

        // Assert
        verify(binanceApiManager).getTickerPrice24(SYMBOL);
        verify(publisher, never()).publishEvent(isA(NewChunkedBuyExecutionEvent.class));
    }
}
