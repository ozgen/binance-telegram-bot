package com.ozgen.telegrambinancebot.adapters.events;


import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.service.telegram.TelegramErrorNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class ErrorEventEventListenerTest {

    @Mock
    private TelegramErrorNotifier telegramErrorNotifier;

    @InjectMocks
    private ErrorEventEventListener errorEventEventListener;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testOnApplicationEvent() {
        ErrorEvent event = new ErrorEvent(this, new RuntimeException());

        this.errorEventEventListener.onApplicationEvent(event);

        verify(this.telegramErrorNotifier)
                .processErrorEvent(event);
    }
}

