package com.ozgen.telegrambinancebot.service;


import com.ozgen.telegrambinancebot.model.ExecutionStrategy;
import com.ozgen.telegrambinancebot.model.TradingStrategy;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.adapters.repository.TradingSignalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class TradingSignalServiceTest {

    @Mock
    private TradingSignalRepository tradingSignalRepository;

    @InjectMocks
    private TradingSignalService tradingSignalService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveTradingSignal_Success() {
        TradingSignal tradingSignal = new TradingSignal();
        when(this.tradingSignalRepository.save(tradingSignal))
                .thenReturn(tradingSignal);

        TradingSignal result = this.tradingSignalService.saveTradingSignal(tradingSignal);

        assertNotNull(result);
        assertEquals(tradingSignal, result);
        verify(this.tradingSignalRepository)
                .save(tradingSignal);
    }

    @Test
    public void testSaveTradingSignal_Exception() {
        TradingSignal tradingSignal = new TradingSignal();
        when(this.tradingSignalRepository.save(any(TradingSignal.class)))
                .thenThrow(new RuntimeException("Test exception"));

        TradingSignal result = this.tradingSignalService.saveTradingSignal(tradingSignal);

        assertEquals(tradingSignal, result);
        verify(this.tradingSignalRepository)
                .save(tradingSignal);
    }

    @Test
    public void testGetTradingSignalsByIdList_Success() {
        List<String> uuidList = List.of(UUID.randomUUID().toString());
        List<TradingStrategy> strategies = List.of(TradingStrategy.DEFAULT);
        List<TradingSignal> expectedSignals = List.of(new TradingSignal());
        when(this.tradingSignalRepository.findAllByIdInAndStrategyInAndExecutionStrategy(uuidList, strategies, ExecutionStrategy.DEFAULT))
                .thenReturn(expectedSignals);

        List<TradingSignal> result = this.tradingSignalService.getTradingSignalsByIdList(uuidList);

        assertEquals(expectedSignals, result);
        verify(this.tradingSignalRepository)
                .findAllByIdInAndStrategyInAndExecutionStrategy(uuidList, strategies, ExecutionStrategy.DEFAULT);
    }

    @Test
    public void testGetTradingSignalsAfterDateAndIsProcessIn_Success() {
        Date date = new Date();
        List<Integer> processStatuses = List.of(1, 2); // Example status codes
        List<TradingSignal> expectedSignals = List.of(new TradingSignal());
        List<TradingStrategy> strategies = List.of(TradingStrategy.DEFAULT);
        when(this.tradingSignalRepository.findAllByCreatedAtAfterAndIsProcessedInAndStrategyInAndExecutionStrategy(date, processStatuses, strategies, ExecutionStrategy.DEFAULT))
                .thenReturn(expectedSignals);

        List<TradingSignal> result = this.tradingSignalService.getDefaultTradingSignalsAfterDateAndIsProcessIn(date, processStatuses);

        assertEquals(expectedSignals, result);
        verify(this.tradingSignalRepository)
                .findAllByCreatedAtAfterAndIsProcessedInAndStrategyInAndExecutionStrategy(date, processStatuses, strategies, ExecutionStrategy.DEFAULT);
    }

    @Test
    public void testGetAllTradingSignalsAfterDateAndIsProcessIn_Success() {
        Date date = new Date();
        List<Integer> processStatuses = List.of(1, 2); // Example status codes
        List<TradingSignal> expectedSignals = List.of(new TradingSignal());
        List<TradingStrategy> strategies = List.of(TradingStrategy.DEFAULT, TradingStrategy.SELL_LATER);
        when(this.tradingSignalRepository.findAllByCreatedAtAfterAndIsProcessedInAndStrategyInAndExecutionStrategy(date, processStatuses, strategies, ExecutionStrategy.DEFAULT))
                .thenReturn(expectedSignals);

        List<TradingSignal> result = this.tradingSignalService.getAllTradingSignalsAfterDateAndIsProcessIn(date, processStatuses);

        assertEquals(expectedSignals, result);
        verify(this.tradingSignalRepository)
                .findAllByCreatedAtAfterAndIsProcessedInAndStrategyInAndExecutionStrategy(date, processStatuses, strategies, ExecutionStrategy.DEFAULT);
    }

    @Test
    public void testGetSellLaterTradingSignalsAfterDateAndIsProcessIn_Success() {
        Date date = new Date();
        List<Integer> processStatuses = List.of(1, 2); // Example status codes
        List<TradingSignal> expectedSignals = List.of(new TradingSignal());
        List<TradingStrategy> strategies = List.of( TradingStrategy.SELL_LATER);
        when(this.tradingSignalRepository.findAllByCreatedAtAfterAndIsProcessedInAndStrategyInAndExecutionStrategy(date, processStatuses, strategies, ExecutionStrategy.DEFAULT))
                .thenReturn(expectedSignals);

        List<TradingSignal> result = this.tradingSignalService.getSellLaterTradingSignalsAfterDateAndIsProcessIn(date, processStatuses);

        assertEquals(expectedSignals, result);
        verify(this.tradingSignalRepository)
                .findAllByCreatedAtAfterAndIsProcessedInAndStrategyInAndExecutionStrategy(date, processStatuses, strategies, ExecutionStrategy.DEFAULT);
    }

    @Test
    public void testUpdateTradingSignal_Success() {
        // Arrange
        TradingSignal signal = new TradingSignal();
        String id = UUID.randomUUID().toString();
        signal.setId(id);
        when(tradingSignalRepository.existsById(id)).thenReturn(true);
        when(tradingSignalRepository.save(signal)).thenReturn(signal);

        // Act
        tradingSignalService.updateTradingSignal(signal);

        // Assert
        verify(tradingSignalRepository).existsById(id);
        verify(tradingSignalRepository).save(signal);
    }

    @Test
    public void testUpdateTradingSignal_IdIsNull() {
        // Arrange
        TradingSignal signal = new TradingSignal(); // ID is null

        // Act
        tradingSignalService.updateTradingSignal(signal);

        // Assert
        verify(tradingSignalRepository, never()).save(any());
        verify(tradingSignalRepository, never()).existsById(any());
    }

    @Test
    public void testUpdateTradingSignal_IdDoesNotExist() {
        // Arrange
        TradingSignal signal = new TradingSignal();
        String id = UUID.randomUUID().toString();
        signal.setId(id);
        when(tradingSignalRepository.existsById(id)).thenReturn(false);

        // Act
        tradingSignalService.updateTradingSignal(signal);

        // Assert
        verify(tradingSignalRepository).existsById(id);
        verify(tradingSignalRepository, never()).save(any());
    }

    @Test
    public void testUpdateTradingSignal_ExceptionThrown() {
        // Arrange
        TradingSignal signal = new TradingSignal();
        String id = UUID.randomUUID().toString();
        signal.setId(id);
        when(tradingSignalRepository.existsById(id)).thenReturn(true);
        when(tradingSignalRepository.save(signal)).thenThrow(new RuntimeException("Simulated DB error"));

        // Act
        tradingSignalService.updateTradingSignal(signal);

        // Assert
        verify(tradingSignalRepository).existsById(id);
        verify(tradingSignalRepository).save(signal);
    }

    @Test
    public void testGetAllTradingSignalsAfterDateAndIsProcessInForChunkOrders_Success() {
        // Arrange
        Date date = new Date();
        List<Integer> processStatuses = List.of(1, 2); // example statuses
        List<TradingStrategy> expectedStrategies = List.of(TradingStrategy.DEFAULT, TradingStrategy.SELL_LATER);
        List<TradingSignal> expectedSignals = List.of(new TradingSignal());

        when(tradingSignalRepository.findAllByCreatedAtAfterAndIsProcessedInAndStrategyInAndExecutionStrategy(
                date, processStatuses, expectedStrategies, ExecutionStrategy.CHUNKED))
                .thenReturn(expectedSignals);

        // Act
        List<TradingSignal> result = tradingSignalService.getAllTradingSignalsAfterDateAndIsProcessInForChunkOrders(date, processStatuses);

        // Assert
        assertEquals(expectedSignals, result);
        verify(tradingSignalRepository).findAllByCreatedAtAfterAndIsProcessedInAndStrategyInAndExecutionStrategy(
                date, processStatuses, expectedStrategies, ExecutionStrategy.CHUNKED);
    }

    @Test
    public void testGetAllTradingSignalsAfterDateAndIsProcessInForPrChunkOrders_Success() {
        // Arrange
        Date date = new Date();
        List<Integer> processStatuses = List.of(0, 1);
        List<TradingStrategy> strategies = List.of(TradingStrategy.DEFAULT, TradingStrategy.SELL_LATER);
        List<TradingSignal> expectedSignals = List.of(new TradingSignal());

        when(tradingSignalRepository.findAllByCreatedAtAfterAndIsProcessedInAndStrategyInAndExecutionStrategy(
                date, processStatuses, strategies, ExecutionStrategy.CHUNKED_PROGRESSIVE))
                .thenReturn(expectedSignals);

        // Act
        List<TradingSignal> result = tradingSignalService.getAllTradingSignalsAfterDateAndIsProcessInForPrChunkOrders(date, processStatuses);

        // Assert
        assertEquals(expectedSignals, result);
        verify(tradingSignalRepository).findAllByCreatedAtAfterAndIsProcessedInAndStrategyInAndExecutionStrategy(
                date, processStatuses, strategies, ExecutionStrategy.CHUNKED_PROGRESSIVE);
    }
}

