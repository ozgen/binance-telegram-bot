package com.ozgen.telegrambinancebot.scheduling.bot;


import com.ozgen.telegrambinancebot.manager.bot.FutureTradeManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class SellErrorFutureTradeSchedulerTest {

    @Mock
    private FutureTradeManager futureTradeManager;

    @InjectMocks
    private SellErrorFutureTradeScheduler sellErrorFutureTradeScheduler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcessSellErrorFutureTrades(){
        this.sellErrorFutureTradeScheduler.processSellErrorFutureTrades();

        verify(this.futureTradeManager)
                .processSellErrorFutureTrades();
    }
}
