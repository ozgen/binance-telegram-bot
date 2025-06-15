package com.ozgen.telegrambinancebot.manager.telegram;


import com.ozgen.telegrambinancebot.configuration.properties.AppConfiguration;
import com.ozgen.telegrambinancebot.model.ExecutionStrategy;
import com.ozgen.telegrambinancebot.model.events.IncomingChunkedTradingSignalEvent;
import com.ozgen.telegrambinancebot.model.events.IncomingTradingSignalEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import com.ozgen.telegrambinancebot.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TelegramMessageManagerTest {

    @Mock
    private TradingSignalService tradingSignalService;
    @Mock
    private AppConfiguration appConfiguration;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private TelegramMessageManager telegramMessageManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testParseTelegramMessage_withValidSignal() {
        // Arrange
        String message = TestData.TRADING_SIGNAL;
        when(appConfiguration.getStrategy()).thenReturn(ExecutionStrategy.DEFAULT);
        when(tradingSignalService.saveTradingSignal(any(TradingSignal.class)))
                .thenAnswer((Answer<TradingSignal>) invocation -> (TradingSignal) invocation.getArguments()[0]);

        // Act
        String result = telegramMessageManager.parseTelegramMessage(message);

        // Assert
        assertTrue(result.contains("success"));
        verify(tradingSignalService)
                .saveTradingSignal(any(TradingSignal.class));
        verify(publisher)
                .publishEvent(any(IncomingTradingSignalEvent.class));
    }

    @Test
    void testParseTelegramMessage_withValidSignalAndChunkStrategy() {
        // Arrange
        String message = TestData.TRADING_SIGNAL;
        when(appConfiguration.getStrategy()).thenReturn(ExecutionStrategy.CHUNKED);
        when(tradingSignalService.saveTradingSignal(any(TradingSignal.class)))
                .thenAnswer((Answer<TradingSignal>) invocation -> (TradingSignal) invocation.getArguments()[0]);

        // Act
        String result = telegramMessageManager.parseTelegramMessage(message);

        // Assert
        assertTrue(result.contains("success"));
        verify(tradingSignalService)
                .saveTradingSignal(any(TradingSignal.class));
        verify(publisher)
                .publishEvent(any(IncomingChunkedTradingSignalEvent.class));
    }

    @Test
    void testParseTelegramMessage_withInvalidSignal() {
        // Arrange
        String message = TestData.INVALID_TRADING_SIGNAL;

        // Act
        String result = telegramMessageManager.parseTelegramMessage(message);

        // Assert
        assertEquals(TelegramMessageManager.FAILED_MESSAGE, result);
        verify(tradingSignalService, never())
                .saveTradingSignal(any());
        verify(publisher, never())
                .publishEvent(any());
    }
}
