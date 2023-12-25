package com.ozgen.telegrambinancebot.adapters.telegram;


import com.ozgen.telegrambinancebot.configuration.telegram.TelegramConfig;
import com.ozgen.telegrambinancebot.manager.telegram.TelegramMessageManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TelegramBinanceBotTest {

    @Mock
    private TelegramMessageManager telegramMessageManager;

    @Mock
    private TelegramConfig telegramConfig;

    private TelegramBinanceBot telegramBinanceBot;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        telegramBinanceBot = new TelegramBinanceBot(telegramMessageManager, telegramConfig);
    }

    @Test
    void testOnUpdateReceivedWithChannelPost() throws Exception {
        String testMessage = "Test Message";
        Long chatId = 12345L;
        String text = "Channel post text";

        Update mockUpdate = mock(Update.class);
        Message mockMessage = mock(Message.class);
        Chat mockChat = mock(Chat.class);

        when(mockUpdate.hasChannelPost()).thenReturn(true);
        when(mockUpdate.getChannelPost()).thenReturn(mockMessage);
        when(mockMessage.getChat()).thenReturn(mockChat);
        when(mockChat.getId()).thenReturn(chatId);
        when(mockMessage.getText()).thenReturn(text);

        when(telegramMessageManager.parseTelegramMessage(text))
                .thenReturn(testMessage);

        telegramBinanceBot.onUpdateReceived(mockUpdate);

        SendMessage expectedSendMessage = new SendMessage();
        expectedSendMessage.setChatId(chatId.toString());
        expectedSendMessage.setText(testMessage);
    }
}
