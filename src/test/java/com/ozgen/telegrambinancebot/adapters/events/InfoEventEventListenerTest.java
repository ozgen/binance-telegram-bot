package com.ozgen.telegrambinancebot.adapters.events;


import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.events.InfoEvent;
import com.ozgen.telegrambinancebot.service.telegram.TelegramSignalNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class InfoEventEventListenerTest {

    @Mock
    private TelegramSignalNotifier telegramSignalNotifier;

    @InjectMocks
    private InfoEventEventListener infoEventEventListener;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testOnApplicationEvent() {
        InfoEvent infoEvent = new InfoEvent(this, new BuyOrder().toString());

        this.infoEventEventListener.onApplicationEvent(infoEvent);

        verify(this.telegramSignalNotifier)
                .processInfoEvent(infoEvent);
    }
}
