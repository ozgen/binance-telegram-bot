package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.model.TradingStrategy;
import com.ozgen.telegrambinancebot.model.binance.AssetBalance;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.bot.ChunkOrder;
import com.ozgen.telegrambinancebot.model.bot.OrderStatus;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.InfoEvent;
import com.ozgen.telegrambinancebot.model.events.NewSellChunkOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BinanceChunkSellOrderManagerTest {

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
    private BinanceChunkSellOrderManager manager;

    private AssetBalance assetBalance;

    private final ChunkOrder chunk = new ChunkOrder();
    private final TradingSignal signal = new TradingSignal();
    private final TickerData tickerData = new TickerData();

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        chunk.setSymbol("BTCUSDT");
        chunk.setBuyPrice(100.0);
        chunk.setBuyCoinAmount(10.0);
        chunk.setTradingSignal(signal);
        chunk.setId("chunk123");

        signal.setStrategy(TradingStrategy.DEFAULT);
        signal.setStopLoss("90");

        tickerData.setLastPrice("110");

        when(botConfiguration.getProfitPercentage()).thenReturn(5.0);
        when(binanceApiManager.getTickerPrice24("BTCUSDT")).thenReturn(tickerData);

        assetBalance = new AssetBalance();
        assetBalance.setAsset("BTC");
        assetBalance.setFree("10.0");
    }

    @Test
    void testProcessChunkOrder_shouldSell() throws Exception {
        AssetBalance asset = new AssetBalance();
        asset.setAsset("BTC");
        asset.setFree("10");

        when(binanceHelper.getUserAssets()).thenReturn(List.of(asset));
        when(botConfiguration.getCurrency()).thenReturn("USDT");

        manager.handleChunkOrder(chunk);

        verify(binanceApiManager).newOrderWithStopLoss(eq("BTCUSDT"), anyDouble(), eq(10.0), eq(90.0));
        verify(botOrderService).saveChunkOrder(chunk);
        verify(publisher).publishEvent(any(InfoEvent.class));
        assertEquals(OrderStatus.SELL_EXECUTED, chunk.getStatus());
    }

    @Test
    void testProcessChunkOrder_shouldRetry() throws Exception {
        // Arrange
        this.chunk.getTradingSignal().setStrategy(TradingStrategy.SELL_LATER);
        when(botConfiguration.getProfitPercentage()).thenReturn(10d);
        when(binanceApiManager.getTickerPrice24("BTCUSDT")).thenReturn(tickerData);
        tickerData.setLastPrice("90");

        when(botConfiguration.getCurrency()).thenReturn("USDT");

        when(binanceHelper.getUserAssets()).thenReturn(List.of(assetBalance));

        // Act
        manager.handleChunkOrder(chunk);

        // Assert
        verify(botOrderService).saveChunkOrder(chunk);
        assertEquals(OrderStatus.SELL_PENDING_RETRY, chunk.getStatus());
        verify(binanceApiManager, never()).newOrderWithStopLoss(any(), anyDouble(), anyDouble(), anyDouble());
        verify(publisher, never()).publishEvent(isA(InfoEvent.class));
    }


    @Test
    void testProcessChunkOrder_shouldSellWithLowerBalance() {
        AssetBalance asset = new AssetBalance();
        asset.setAsset("BTC");
        asset.setFree("5");

        when(binanceHelper.getUserAssets()).thenReturn(List.of(asset));
        when(botConfiguration.getCurrency()).thenReturn("USDT");

        manager.handleChunkOrder(chunk);

        ArgumentCaptor<ChunkOrder> captor = ArgumentCaptor.forClass(ChunkOrder.class);
        verify(botOrderService).saveChunkOrder(captor.capture());
        assertEquals(5.0, captor.getValue().getSellCoinAmount());
    }

    @Test
    void testProcessChunkOrder_shouldFail_nullSymbol() {
        when(botConfiguration.getCurrency()).thenReturn("USDT");
        chunk.setSymbol("XYZUNKNOWN");
        when(binanceHelper.getUserAssets()).thenReturn(Collections.emptyList());

        manager.handleChunkOrder(chunk);

        verify(botOrderService).saveChunkOrder(chunk);
        verify(publisher).publishEvent(any(ErrorEvent.class));
    }

    @Test
    void testProcessChunkOrder_shouldFail_onException() throws Exception {
        when(binanceApiManager.getTickerPrice24("BTCUSDT")).thenThrow(new RuntimeException("Binance API error"));

        manager.handleChunkOrder(chunk);

        verify(publisher).publishEvent(any(ErrorEvent.class));
        verify(botOrderService).saveChunkOrder(chunk);
        assertEquals(OrderStatus.SELL_FAILED, chunk.getStatus());
    }

    @Test
    void testRetryPendingChunks_priceRises() throws Exception {
        chunk.setBuyPrice(100);
        when(botOrderService.getBuyChunksByStatus(OrderStatus.SELL_PENDING_RETRY)).thenReturn(List.of(chunk));
        when(binanceApiManager.getTickerPrice24("BTCUSDT")).thenReturn(tickerData);
        tickerData.setLastPrice("120");

        manager.retryPendingChunks();

        verify(publisher).publishEvent(any(NewSellChunkOrderEvent.class));
    }

    @Test
    void testRetryPendingChunks_priceFalls() {
        chunk.setBuyPrice(100);
        when(botOrderService.getBuyChunksByStatus(OrderStatus.SELL_PENDING_RETRY)).thenReturn(List.of(chunk));
        tickerData.setLastPrice("90");

        manager.retryPendingChunks();

        verify(publisher, never()).publishEvent(any(NewSellChunkOrderEvent.class));
    }

    @Test
    void testRetryPendingChunks_withException() throws Exception {
        chunk.setBuyPrice(100);
        when(botOrderService.getBuyChunksByStatus(OrderStatus.SELL_PENDING_RETRY)).thenReturn(List.of(chunk));
        when(binanceApiManager.getTickerPrice24("BTCUSDT")).thenThrow(new RuntimeException("fail"));

        manager.retryPendingChunks();

        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void testProcessSellChunkOrders_shouldProcessAllReturnedChunks() {
        // Arrange
        Date searchDate = new Date();
        ChunkOrder chunk1 = new ChunkOrder();
        chunk1.setId("chunk1");
        ChunkOrder chunk2 = new ChunkOrder();
        chunk2.setId("chunk2");

        when(binanceHelper.getSearchDate()).thenReturn(searchDate);
        when(botOrderService.getBuyChunksByStatusesAndDate(
                eq(List.of(OrderStatus.SELL_PENDING_RETRY, OrderStatus.SELL_FAILED)),
                eq(searchDate)))
                .thenReturn(List.of(chunk1, chunk2));

        // Use spy to verify internal method call
        BinanceChunkSellOrderManager spyManager = Mockito.spy(manager);

        // Act
        spyManager.processSellChunkOrders();

        // Assert
        verify(spyManager).handleChunkOrder(chunk1);
        verify(spyManager).handleChunkOrder(chunk2);
        verify(botOrderService).getBuyChunksByStatusesAndDate(anyList(), eq(searchDate));
    }

}
