package com.ozgen.telegrambinancebot.service;


import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.repository.TradingSignalRepository;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TradingSignalServiceTest {

    @Mock
    private TradingSignalRepository tradingSignalRepository;

    @InjectMocks
    private TradingSignalService tradingSignalService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
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
        List<UUID> uuidList = List.of(UUID.randomUUID());
        List<TradingSignal> expectedSignals = List.of(new TradingSignal());
        when(this.tradingSignalRepository.findAllByIdIn(uuidList))
                .thenReturn(expectedSignals);

        List<TradingSignal> result = this.tradingSignalService.getTradingSignalsByIdList(uuidList);

        assertEquals(expectedSignals, result);
        verify(this.tradingSignalRepository)
                .findAllByIdIn(uuidList);
    }

    @Test
    public void testGetTradingSignalsAfterDateAndIsProcessIn_Success() {
        Date date = new Date();
        List<Integer> processStatuses = List.of(1, 2); // Example status codes
        List<TradingSignal> expectedSignals = List.of(new TradingSignal());
        when(this.tradingSignalRepository.findAllByCreatedAtAfterAndIsProcessedIn(date, processStatuses))
                .thenReturn(expectedSignals);

        List<TradingSignal> result = this.tradingSignalService.getTradingSignalsAfterDateAndIsProcessIn(date, processStatuses);

        assertEquals(expectedSignals, result);
        verify(this.tradingSignalRepository)
                .findAllByCreatedAtAfterAndIsProcessedIn(date, processStatuses);
    }
}

