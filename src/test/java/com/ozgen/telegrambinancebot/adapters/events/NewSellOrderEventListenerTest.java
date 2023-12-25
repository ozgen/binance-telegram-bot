package com.ozgen.telegrambinancebot.adapters.events;


import com.ozgen.telegrambinancebot.manager.binance.BinanceSellOrderManager;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class NewSellOrderEventListenerTest {
    @Mock
    private BinanceSellOrderManager binanceSellOrderManager;

    @InjectMocks
    private NewSellOrderEventListener newSellOrderEventListener;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testOnApplicationEvent() {
        NewSellOrderEvent event = new NewSellOrderEvent(this, new BuyOrder());

        this.newSellOrderEventListener.onApplicationEvent(event);

        verify(this.binanceSellOrderManager)
                .processNewSellOrderEvent(event);
    }
}
