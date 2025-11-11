package com.ozgen.telegrambinancebot.service;


import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.adapters.repository.TickerDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TickerDataServiceTest {

    @Mock
    private TickerDataRepository tickerDataRepository;

    @InjectMocks
    private TickerDataService tickerDataService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateTickerData_Success() {
        TickerData tickerData = new TickerData();
        when(this.tickerDataRepository.save(tickerData))
                .thenReturn(tickerData);

        TickerData result = this.tickerDataService.createTickerData(tickerData);

        assertNotNull(result);
        assertEquals(tickerData, result);
        verify(this.tickerDataRepository)
                .save(tickerData);
    }

    @Test
    public void testCreateTickerData_Exception() {
        TickerData tickerData = new TickerData();
        when(this.tickerDataRepository.save(any(TickerData.class)))
                .thenThrow(new RuntimeException("Test exception"));

        TickerData result = this.tickerDataService.createTickerData(tickerData);

        assertEquals(tickerData, result);
        verify(this.tickerDataRepository)
                .save(tickerData);
    }

}
