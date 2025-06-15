package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.ScheduleConfiguration;
import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
import com.ozgen.telegrambinancebot.model.bot.ChunkOrder;
import com.ozgen.telegrambinancebot.model.bot.OrderStatus;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.NewProgressiveChunkedSellOrderEvent;
import com.ozgen.telegrambinancebot.model.events.NewSellChunkOrderEvent;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BinanceOpenProgressiveChunkBuyOrderManagerTest {

    @Mock
    private BinanceApiManager binanceApiManager;

    @Mock
    private BotOrderService botOrderService;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private ScheduleConfiguration scheduleConfiguration;

    private BinanceOpenProgressiveChunkBuyOrderManager manager;

    private AutoCloseable mocks;

    @BeforeEach
    void setup() {
        mocks = MockitoAnnotations.openMocks(this);
        when(scheduleConfiguration.getMonthBefore()).thenReturn(1);
        manager = new BinanceOpenProgressiveChunkBuyOrderManager(
                binanceApiManager, botOrderService, publisher, scheduleConfiguration);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void testProcessOpenChunk_matchingOrderFound_executesSuccessfully() throws Exception {
        ChunkOrder chunk = new ChunkOrder();
        chunk.setId("chunk-1");
        chunk.setSymbol("BTCUSDT");
        chunk.setBuyCoinAmount(0.01);
        chunk.setBuyPrice(30000.0);
        chunk.setStatus(OrderStatus.BUY_FAILED);

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderId(12345L); // updated to long
        orderInfo.setOrigQty("0.01");
        orderInfo.setExecutedQty("0.005");

        when(binanceApiManager.getOpenOrders("BTCUSDT")).thenReturn(List.of(orderInfo));

        manager.processOpenProgressiveChunk(chunk);

        verify(binanceApiManager).cancelAndNewOrderWithStopLoss(eq("BTCUSDT"), eq(30000.0), eq(0.005), eq(12345L));
        verify(botOrderService).saveChunkOrder(argThat(c -> c.getStatus() == OrderStatus.BUY_EXECUTED));
        verify(publisher).publishEvent(isA(NewProgressiveChunkedSellOrderEvent.class));
    }

    @Test
    void testProcessOpenChunk_noMatchingOrder_foundLogsAndSkips() throws Exception {
        ChunkOrder chunk = new ChunkOrder();
        chunk.setId("chunk-2");
        chunk.setSymbol("BTCUSDT");
        chunk.setBuyCoinAmount(0.01);

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderId(12345L);
        orderInfo.setOrigQty("0.02");  // Different qty

        when(binanceApiManager.getOpenOrders("BTCUSDT")).thenReturn(List.of(orderInfo));

        manager.processOpenProgressiveChunk(chunk);

        verify(binanceApiManager).getOpenOrders("BTCUSDT");
        verify(publisher, never()).publishEvent(isA(NewSellChunkOrderEvent.class));
    }

    @Test
    void testProcessOpenChunk_invalidQty_throwsAndLogs() throws Exception {
        ChunkOrder chunk = new ChunkOrder();
        chunk.setId("chunk-3");
        chunk.setSymbol("BTCUSDT");
        chunk.setBuyCoinAmount(0.01);

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderId(12345L);
        orderInfo.setOrigQty("INVALID");
        orderInfo.setExecutedQty("0.005");

        when(binanceApiManager.getOpenOrders("BTCUSDT")).thenReturn(List.of(orderInfo));

        manager.processOpenProgressiveChunk(chunk);

        verify(botOrderService, never()).saveChunkOrder(any());
    }

    @Test
    void testProcessOpenChunk_binanceApiThrows_errorIsLogged() throws Exception {
        ChunkOrder chunk = new ChunkOrder();
        chunk.setId("chunk-4");
        chunk.setSymbol("BTCUSDT");

        when(binanceApiManager.getOpenOrders("BTCUSDT"))
                .thenThrow(new RuntimeException("Binance API failed"));

        manager.processOpenProgressiveChunk(chunk);

        verify(publisher).publishEvent(isA(ErrorEvent.class));
    }

    @Test
    void shouldSkipWhenNoMatchingOpenOrderFound() throws Exception {
        ChunkOrder chunk = new ChunkOrder();
        chunk.setId("chunk-2");
        chunk.setSymbol("ETHUSDT");
        chunk.setBuyCoinAmount(0.05);

        OrderInfo openOrder = new OrderInfo();
        openOrder.setOrigQty("0.10");
        openOrder.setExecutedQty("0.0");

        when(botOrderService.getBuyChunksByStatusesAndDate(any(), any())).thenReturn(List.of(chunk));
        when(binanceApiManager.getOpenOrders("ETHUSDT")).thenReturn(List.of(openOrder));

        manager.processOpenProgressiveChunks();

        verify(publisher, never()).publishEvent(any(NewSellChunkOrderEvent.class));
    }

    @Test
    void testProcessOpenProgressiveChunks_successAndFailure() throws Exception {
        // Given
        ChunkOrder successChunk = new ChunkOrder();
        successChunk.setId("chunk-success");
        successChunk.setSymbol("BTCUSDT");
        successChunk.setBuyCoinAmount(0.01);
        successChunk.setBuyPrice(27000.0);

        ChunkOrder failChunk = new ChunkOrder();
        failChunk.setId("chunk-fail");
        failChunk.setSymbol("ETHUSDT");

        when(scheduleConfiguration.getMonthBefore()).thenReturn(1);

        when(botOrderService.getProgressiveChunksByStatusesAndDate(
                anyList(), any())
        ).thenReturn(List.of(successChunk, failChunk));

        // Mocks for successChunk
        OrderInfo successOrderInfo = new OrderInfo();
        successOrderInfo.setOrigQty("0.01");
        successOrderInfo.setExecutedQty("0.002");
        successOrderInfo.setOrderId(123456L);

        when(binanceApiManager.getOpenOrders("BTCUSDT")).thenReturn(List.of(successOrderInfo));

        // We assume ETHUSDT (failChunk) throws an exception to simulate a failure
        when(binanceApiManager.getOpenOrders("ETHUSDT")).thenThrow(new RuntimeException("Mocked error"));

        // When
        manager.processOpenProgressiveChunks();

        // Then
        // Success chunk should trigger order update and sell event
        verify(binanceApiManager).cancelAndNewOrderWithStopLoss("BTCUSDT", 27000.0, 0.008, 123456L);
        verify(botOrderService).saveChunkOrder(successChunk);
        verify(publisher).publishEvent(argThat(e -> e instanceof NewProgressiveChunkedSellOrderEvent));

        // Fail chunk should publish an ErrorEvent
        verify(publisher, atLeastOnce()).publishEvent(argThat(e -> e instanceof ErrorEvent));
    }
}
