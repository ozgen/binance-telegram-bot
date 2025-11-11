package com.ozgen.telegrambinancebot.service.telegram;


import com.ozgen.telegrambinancebot.adapters.telegram.TelegramErrorBot;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TelegramErrorNotifierTest {

    private String ERROR_MSG = "Test";
    private Long CHANNEL_ID = 1L;


    @Mock
    private TelegramErrorBot telegramBot;

    @InjectMocks
    private TelegramErrorNotifier telegramErrorNotifier;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessErrorEvent_Success() throws TelegramApiException {
        // Arrange
        when(this.telegramBot.getChannelId())
                .thenReturn(CHANNEL_ID);
        ErrorEvent errorEvent = new ErrorEvent(this, new RuntimeException(ERROR_MSG));

        // Act
        this.telegramErrorNotifier.processErrorEvent(errorEvent);

        // Assert
        ArgumentCaptor<SendMessage> msgCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(this.telegramBot)
                .execute(msgCaptor.capture());
        SendMessage sendMessage = msgCaptor.getValue();
        assertEquals(String.valueOf(CHANNEL_ID), sendMessage.getChatId());
        assertEquals(ERROR_MSG, sendMessage.getText());
    }

    @Test
    public void testProcessErrorEvent_withNullChatId() throws TelegramApiException {
        // Arrange
        when(this.telegramBot.getChannelId())
                .thenReturn(null);
        ErrorEvent errorEvent = new ErrorEvent(this, new RuntimeException(ERROR_MSG));

        // Act
        this.telegramErrorNotifier.processErrorEvent(errorEvent);

        // Assert
        verify(this.telegramBot, never())
                .execute(any(SendMessage.class));
    }

    @Test
    public void testProcessErrorEvent_withTelegramApiException() throws TelegramApiException {
        // Arrange
        when(this.telegramBot.getChannelId())
                .thenReturn(CHANNEL_ID);
        when(this.telegramBot.execute(any(SendMessage.class)))
                .thenThrow(TelegramApiException.class);
        ErrorEvent errorEvent = new ErrorEvent(this, new RuntimeException(ERROR_MSG));

        // Act
        this.telegramErrorNotifier.processErrorEvent(errorEvent);

        // Assert
        ArgumentCaptor<SendMessage> msgCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(this.telegramBot)
                .execute(msgCaptor.capture());
        SendMessage sendMessage = msgCaptor.getValue();
        assertEquals(String.valueOf(CHANNEL_ID), sendMessage.getChatId());
        assertEquals(ERROR_MSG, sendMessage.getText());
    }
}
