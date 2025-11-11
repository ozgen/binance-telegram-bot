package com.ozgen.telegrambinancebot.service.telegram;


import com.ozgen.telegrambinancebot.adapters.telegram.TelegramBinanceBot;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.events.InfoEvent;
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

public class TelegramSignalNotifierTest {

    private Long CHANNEL_ID = 1L;


    @Mock
    private TelegramBinanceBot telegramBot;

    @InjectMocks
    private TelegramSignalNotifier telegramSignalNotifier;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessErrorEvent_Success() throws TelegramApiException {
        // Arrange
        BuyOrder buyOrder = new BuyOrder();
        String message = buyOrder.toString();
        when(this.telegramBot.getChannelId())
                .thenReturn(CHANNEL_ID);
        InfoEvent infoEvent = new InfoEvent(this, message);

        // Act
        this.telegramSignalNotifier.processInfoEvent(infoEvent);

        // Assert
        ArgumentCaptor<SendMessage> msgCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(this.telegramBot)
                .execute(msgCaptor.capture());
        SendMessage sendMessage = msgCaptor.getValue();
        assertEquals(String.valueOf(CHANNEL_ID), sendMessage.getChatId());
        assertEquals(message, sendMessage.getText());
    }

    @Test
    public void testProcessErrorEvent_withNullChatId() throws TelegramApiException {
        // Arrange
        BuyOrder buyOrder = new BuyOrder();
        String message = buyOrder.toString();
        when(this.telegramBot.getChannelId())
                .thenReturn(null);
        InfoEvent infoEvent = new InfoEvent(this, message);

        // Act
        this.telegramSignalNotifier.processInfoEvent(infoEvent);

        // Assert
        verify(this.telegramBot, never())
                .execute(any(SendMessage.class));
    }

    @Test
    public void testProcessErrorEvent_withTelegramApiException() throws TelegramApiException {
        // Arrange
        BuyOrder buyOrder = new BuyOrder();
        String message = buyOrder.toString();
        when(this.telegramBot.getChannelId())
                .thenReturn(CHANNEL_ID);
        when(this.telegramBot.execute(any(SendMessage.class)))
                .thenThrow(TelegramApiException.class);
        InfoEvent infoEvent = new InfoEvent(this, message);

        // Act
        this.telegramSignalNotifier.processInfoEvent(infoEvent);

        // Assert
        ArgumentCaptor<SendMessage> msgCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(this.telegramBot)
                .execute(msgCaptor.capture());
        SendMessage sendMessage = msgCaptor.getValue();
        assertEquals(String.valueOf(CHANNEL_ID), sendMessage.getChatId());
        assertEquals(message, sendMessage.getText());
    }
}
