package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.model.TradingStrategy;
import com.ozgen.telegrambinancebot.model.binance.AssetBalance;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.bot.ChunkOrder;
import com.ozgen.telegrambinancebot.model.bot.OrderStatus;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.InfoEvent;
import com.ozgen.telegrambinancebot.model.events.NewProgressiveChunkedSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class BinanceProgressiveChunkedSellOrderManagerTest {

    @Mock
    private BinanceApiManager binanceApiManager;

    @Mock
    private BotOrderService botOrderService;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private BotConfiguration botConfiguration;

    @Mock
    private BinanceHelper binanceHelper;

    @InjectMocks
    private BinanceProgressiveChunkedSellOrderManager manager;

    private ChunkOrder chunk;
    private TradingSignal signal;
    private TickerData ticker;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        signal = new TradingSignal();
        signal.setStopLoss("90");
        signal.setStrategy(TradingStrategy.DEFAULT);
        signal.setTakeProfits(List.of("110", "120", "130"));

        chunk = new ChunkOrder();
        chunk.setSymbol("BTCUSDT");
        chunk.setBuyPrice(100.0);
        chunk.setBuyCoinAmount(10.0);
        chunk.setTakeProfitIndex(0);
        chunk.setTradingSignal(signal);
        chunk.setId("chunk123");

        ticker = new TickerData();
        ticker.setLastPrice("115");

        when(binanceApiManager.getTickerPrice24("BTCUSDT")).thenReturn(ticker);
        when(botConfiguration.getProfitPercentage()).thenReturn(5.0);
    }

    @Test
    void testOnNewSellProgressiveChunkOrderEvent_shouldDelegateToProcessChunkOrder() {
        // Arrange
        ChunkOrder mockChunkOrder = new ChunkOrder();
        mockChunkOrder.setId("chunk123");

        NewProgressiveChunkedSellOrderEvent event = new NewProgressiveChunkedSellOrderEvent(this, mockChunkOrder);

        BinanceProgressiveChunkedSellOrderManager spyManager = Mockito.spy(manager);

        // Act
        spyManager.onNewSellProgressiveChunkOrderEvent(event);

        // Assert
        verify(spyManager).processChunkOrder(mockChunkOrder);
    }


    @Test
    void testProcessChunkOrder_shouldSell() {
        AssetBalance balance = new AssetBalance();
        balance.setAsset("BTC");
        balance.setFree("10");
        when(binanceHelper.getUserAssets()).thenReturn(List.of(balance));
        when(binanceHelper.calculateSellAmount(any(), any())).thenReturn(10.0);
        when(botOrderService.getProgressiveChunksByStatusesAndDate(any(), any())).thenReturn(List.of(chunk));

        manager.processSellChunkOrders();

        verify(botOrderService, atLeastOnce()).saveChunkOrder(any());
        verify(botOrderService).getProgressiveChunksByStatusesAndDate(any(), any());
        verify(publisher).publishEvent(any(InfoEvent.class));
    }

    @Test
    void testProcessChunkOrder_shouldRetryDueToLowPriceWithSellLater() {
        ticker.setLastPrice("95");
        signal.setStrategy(TradingStrategy.SELL_LATER);
        when(botOrderService.getProgressiveChunksByStatusesAndDate(any(), any())).thenReturn(List.of(chunk));

        manager.processSellChunkOrders();

        verify(botOrderService, atLeastOnce()).saveChunkOrder(any());
        verify(botOrderService).getProgressiveChunksByStatusesAndDate(any(), any());
        assertEquals(OrderStatus.SELL_PENDING_RETRY, chunk.getStatus());
        verify(publisher, never()).publishEvent(isA(InfoEvent.class));
    }

    @Test
    void testProcessChunkOrder_shouldRetryDueToLowPriceDefault() {
        ticker.setLastPrice("95");
        signal.setStrategy(TradingStrategy.DEFAULT);
        AssetBalance balance = new AssetBalance();
        balance.setAsset("BTC");
        balance.setFree("10");
        when(binanceHelper.getUserAssets()).thenReturn(List.of(balance));
        when(binanceHelper.calculateSellAmount(any(), any())).thenReturn(10.0);
        when(botOrderService.getProgressiveChunksByStatusesAndDate(any(), any())).thenReturn(List.of(chunk));

        manager.processSellChunkOrders();

        verify(botOrderService, atLeastOnce()).saveChunkOrder(any());
        verify(botOrderService).getProgressiveChunksByStatusesAndDate(any(), any());
        assertEquals(OrderStatus.SELL_EXECUTED, chunk.getStatus());
        verify(publisher).publishEvent(isA(InfoEvent.class));
    }

    @Test
    void testProcessChunkOrder_shouldFail() throws Exception {
        when(binanceApiManager.getTickerPrice24("BTCUSDT"))
                .thenThrow(new RuntimeException("Price fetch failed"));
        when(botOrderService.getProgressiveChunksByStatusesAndDate(any(), any())).thenReturn(List.of(chunk));

        manager.processSellChunkOrders();

        verify(botOrderService, atLeastOnce()).saveChunkOrder(any());
        verify(publisher).publishEvent(any(ErrorEvent.class));
    }

    @Test
    void testRetryPendingChunks_shouldTriggerEvent() throws Exception {
        chunk.setBuyPrice(100);
        ticker.setLastPrice("105");
        when(botOrderService.getBuyChunksByStatus(OrderStatus.SELL_PENDING_RETRY)).thenReturn(List.of(chunk));
        when(binanceApiManager.getTickerPrice24("BTCUSDT")).thenReturn(ticker);

        manager.retryPendingChunks();

        verify(publisher).publishEvent(any(NewProgressiveChunkedSellOrderEvent.class));
    }

    @Test
    void testRetryPendingChunks_shouldSkipEvent() {
        chunk.setBuyPrice(110);
        ticker.setLastPrice("100");
        when(botOrderService.getBuyChunksByStatus(OrderStatus.SELL_PENDING_RETRY)).thenReturn(List.of(chunk));

        manager.retryPendingChunks();

        verify(publisher, never()).publishEvent(any(NewProgressiveChunkedSellOrderEvent.class));
    }

    @Test
    void testRetryPendingChunks_shouldHandleException() throws Exception {
        when(botOrderService.getBuyChunksByStatus(OrderStatus.SELL_PENDING_RETRY)).thenReturn(List.of(chunk));
        when(binanceApiManager.getTickerPrice24("BTCUSDT")).thenThrow(new RuntimeException("fail"));

        manager.retryPendingChunks();

        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void testProcessSellChunkOrders_shouldIterateAll() {
        ChunkOrder c1 = new ChunkOrder();
        c1.setSymbol("BTCUSDT");
        c1.setId("1");
        c1.setBuyPrice(100.0);
        c1.setTradingSignal(signal);

        ChunkOrder c2 = new ChunkOrder();
        c2.setSymbol("BTCUSDT");
        c2.setId("2");
        c2.setBuyPrice(100.0);
        c2.setTradingSignal(signal);

        when(binanceHelper.getSearchDate()).thenReturn(new Date());
        when(botOrderService.getProgressiveChunksByStatusesAndDate(any(), any())).thenReturn(List.of(c1, c2));
        BinanceProgressiveChunkedSellOrderManager spyManager = spy(manager);

        spyManager.processSellChunkOrders();

        verify(spyManager).processChunkOrder(c1);
        verify(spyManager).processChunkOrder(c2);
    }
}
