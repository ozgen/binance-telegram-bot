package com.ozgen.telegrambinancebot.scheduling.binance;


import com.ozgen.telegrambinancebot.manager.binance.BinanceOpenBuyOrderManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class BinanceOpenBuyOrderSchedulerTest {

    @Mock
    private BinanceOpenBuyOrderManager binanceOpenBuyOrderManager;

    @InjectMocks
    private BinanceOpenBuyOrderScheduler binanceOpenBuyOrderScheduler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessOpenBuyOrders() {
        this.binanceOpenBuyOrderScheduler.processOpenBuyOrders();

        verify(this.binanceOpenBuyOrderManager)
                .processOpenBuyOrders();
    }

}
