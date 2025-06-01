package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.ScheduleConfiguration;
import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
import com.ozgen.telegrambinancebot.model.bot.ChunkOrder;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.NewSellChunkOrderEvent;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BinanceOpenBuyChunkOrderManagerTest {

    @Mock
    private BinanceApiManager binanceApiManager;

    @Mock
    private BotOrderService botOrderService;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private ScheduleConfiguration scheduleConfiguration;

    @InjectMocks
    private BinanceOpenBuyChunkOrderManager manager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessOpenChunkOrders_successfulRecovery() throws Exception {
        ChunkOrder chunk = new ChunkOrder();
        chunk.setId("chunk1");
        chunk.setSymbol("BTCUSDT");
        chunk.setBuyCoinAmount(10.0);
        chunk.setBuyPrice(30000.0);

        OrderInfo order = new OrderInfo();
        order.setOrderId(123L);
        order.setOrigQty("10.0");
        order.setExecutedQty("2.0");

        when(scheduleConfiguration.getMonthBefore()).thenReturn(1);
        when(botOrderService.getBuyChunksByStatusesAndDate(any(), any(Date.class))).thenReturn(List.of(chunk));
        when(binanceApiManager.getOpenOrders("BTCUSDT")).thenReturn(List.of(order));

        manager.processOpenChunkOrders();

        verify(binanceApiManager).cancelAndNewOrderWithStopLoss("BTCUSDT", 30000.0, 8.0, 123L);
        verify(botOrderService).saveChunkOrder(chunk);
        verify(publisher).publishEvent(any(NewSellChunkOrderEvent.class));
    }


    @Test
    void testProcessOpenChunkOrders_noMatchingOrder() throws Exception {
        ChunkOrder chunk = new ChunkOrder();
        chunk.setId("chunk2");
        chunk.setSymbol("BTCUSDT");
        chunk.setBuyCoinAmount(10.0);
        chunk.setBuyPrice(30000d);

        OrderInfo order = new OrderInfo();
        order.setOrderId(1L);
        order.setOrigQty("5");
        order.setExecutedQty("0");

        when(scheduleConfiguration.getMonthBefore()).thenReturn(1);
        when(botOrderService.getBuyChunksByStatusesAndDate(
                ArgumentMatchers.anyList(), ArgumentMatchers.any(Date.class)))
                .thenReturn(List.of(chunk));
        when(binanceApiManager.getOpenOrders("BTCUSDT")).thenReturn(List.of(order));

        manager.processOpenChunkOrders();

        verify(binanceApiManager, never()).cancelAndNewOrderWithStopLoss(any(), any(), any(), any());
        verify(botOrderService, never()).saveChunkOrder(any());
        verify(publisher, never()).publishEvent(ArgumentMatchers.any(NewSellChunkOrderEvent.class));
    }

    @Test
    void testProcessOpenChunkOrders_exceptionDuringGetOpenOrders() throws Exception {
        ChunkOrder chunk = new ChunkOrder();
        chunk.setId("chunk3");
        chunk.setSymbol("BTCUSDT");
        chunk.setBuyCoinAmount(10.0);
        chunk.setBuyPrice(30000);

        when(scheduleConfiguration.getMonthBefore()).thenReturn(1);
        when(botOrderService.getBuyChunksByStatusesAndDate(
                ArgumentMatchers.anyList(), ArgumentMatchers.any(Date.class)))
                .thenReturn(List.of(chunk));
        when(binanceApiManager.getOpenOrders("BTCUSDT")).thenThrow(new RuntimeException("API error"));

        manager.processOpenChunkOrders();

        verify(publisher).publishEvent(ArgumentMatchers.any(ErrorEvent.class));
    }

    @Test
    void testProcessOpenChunk_exceptionDuringRecovery() throws Exception {
        ChunkOrder chunk = new ChunkOrder();
        chunk.setId("chunk4");
        chunk.setSymbol("BTCUSDT");
        chunk.setBuyCoinAmount(10.0);
        chunk.setBuyPrice(30000d);

        OrderInfo order = new OrderInfo();
        order.setOrderId(1L);
        order.setOrigQty("10.0");
        order.setExecutedQty("1");

        when(binanceApiManager.getOpenOrders("BTCUSDT")).thenReturn(List.of(order));
        doThrow(new RuntimeException("Cancel error"))
                .when(binanceApiManager)
                .cancelAndNewOrderWithStopLoss(anyString(), anyDouble(), anyDouble(), anyLong());

        manager.processOpenChunk(chunk);

        verify(publisher).publishEvent(argThat(event -> event instanceof ErrorEvent));
    }
}
