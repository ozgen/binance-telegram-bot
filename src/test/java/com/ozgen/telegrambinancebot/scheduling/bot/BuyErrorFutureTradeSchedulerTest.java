package com.ozgen.telegrambinancebot.scheduling.bot;


import com.ozgen.telegrambinancebot.manager.bot.FutureTradeManager;
import com.ozgen.telegrambinancebot.model.TradeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class BuyErrorFutureTradeSchedulerTest {

    @Mock
    private FutureTradeManager futureTradeManager;

    @InjectMocks
    private BuyErrorFutureTradeScheduler buyErrorFutureTradeScheduler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessBuyErrorFutureTrades(){
        this.buyErrorFutureTradeScheduler.processBuyErrorFutureTrades();

        verify(this.futureTradeManager)
                .processFutureTrades(TradeStatus.ERROR_BUY);
    }
}
