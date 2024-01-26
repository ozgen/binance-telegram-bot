package com.ozgen.telegrambinancebot.scheduling.binance;

import com.ozgen.telegrambinancebot.manager.binance.BinanceOpenSellOrderManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;


public class BinanceNotCompletedSellOrderSchedulerTest {

    @Mock
    private BinanceOpenSellOrderManager binanceOpenSellOrderManager;

    @InjectMocks
    private BinanceNotCompletedSellOrderScheduler binanceNotCompletedSellOrderScheduler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcessOpenSellOrders() {
        this.binanceNotCompletedSellOrderScheduler.processNotCompletedSellOrders();

        verify(this.binanceOpenSellOrderManager)
                .processNotCompletedSellOrders();
    }

}
