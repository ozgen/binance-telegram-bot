package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.AppConfiguration;
import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.model.binance.IntervalType;
import com.ozgen.telegrambinancebot.model.binance.KlineData;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.bot.ChunkOrder;
import com.ozgen.telegrambinancebot.model.bot.OrderStatus;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.InfoEvent;
import com.ozgen.telegrambinancebot.model.events.NewProgressiveChunkedBuyOrderEvent;
import com.ozgen.telegrambinancebot.model.events.NewProgressiveChunkedSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BinanceProgressiveChunkedBuyOrderManagerTest {

    @Mock
    private BinanceApiManager binanceApiManager;
    @Mock
    private BotOrderService botOrderService;
    @Mock
    private BinanceHelper binanceHelper;
    @Mock
    private ApplicationEventPublisher publisher;
    @Mock
    private BotConfiguration botConfig;
    @Mock
    private AppConfiguration appConfig;

    @InjectMocks
    private BinanceProgressiveChunkedBuyOrderManager manager;

    @Captor
    private ArgumentCaptor<ErrorEvent> errorEventCaptor;

    private final TradingSignal signal = new TradingSignal();
    private final TickerData tickerData = new TickerData();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        signal.setId("signal123");
        signal.setSymbol("BTCUSDT");
        signal.setEntryStart("110.0");
        signal.setEntryEnd("90.0");

        tickerData.setLastPrice("100.0");
    }

    @Test
    void testShouldTriggerNextChunk_whenNotLastChunkAndPriceInRange_returnsTrue() throws Exception {
        TradingSignal signal = new TradingSignal();
        signal.setEntryStart("100");
        signal.setEntryEnd("120");

        ChunkOrder order = new ChunkOrder();
        order.setTradingSignal(signal);
        order.setSymbol("BTCUSDT");
        order.setChunkIndex(1);
        order.setTotalChunkCount(5);
        order.setId("chunk-123");

        TickerData tickerData = new TickerData();
        tickerData.setLastPrice("110");
        when(binanceApiManager.getTickerPrice24("BTCUSDT")).thenReturn(tickerData);

        assertTrue(manager.shouldTriggerNextChunk(order));
    }

    @Test
    void testShouldTriggerNextChunk_whenLastChunk_returnsFalse() {
        ChunkOrder order = new ChunkOrder();
        order.setChunkIndex(4);
        order.setTotalChunkCount(5);
        order.setId("chunk-123");

        assertFalse(manager.shouldTriggerNextChunk(order));
    }

    @Test
    void testShouldTriggerNextChunk_whenPriceOutOfRange_returnsFalse() throws Exception {
        TradingSignal signal = new TradingSignal();
        signal.setEntryStart("100");
        signal.setEntryEnd("120");

        ChunkOrder order = new ChunkOrder();
        order.setTradingSignal(signal);
        order.setSymbol("BTCUSDT");
        order.setChunkIndex(1);
        order.setTotalChunkCount(5);
        order.setId("chunk-456");

        TickerData ticker = new TickerData();
        ticker.setLastPrice("130");
        when(binanceApiManager.getTickerPrice24("BTCUSDT")).thenReturn(ticker);

        assertFalse(manager.shouldTriggerNextChunk(order));
    }

    @Test
    void testShouldRetryFailedChunk_whenPriceInRange_returnsTrue() throws Exception {
        TradingSignal signal = new TradingSignal();
        signal.setEntryStart("50");
        signal.setEntryEnd("150");

        ChunkOrder order = new ChunkOrder();
        order.setSymbol("ETHUSDT");
        order.setTradingSignal(signal);
        order.setId("chunk-789");

        TickerData ticker = new TickerData();
        ticker.setLastPrice("100");
        when(binanceApiManager.getTickerPrice24("ETHUSDT")).thenReturn(ticker);

        assertTrue(manager.shouldRetryFailedChunk(order));
    }

    @Test
    void testShouldRetryFailedChunk_whenExceptionOccurs_returnsFalse() throws Exception {
        ChunkOrder order = new ChunkOrder();
        order.setSymbol("FOO");
        order.setTradingSignal(new TradingSignal());
        order.setId("chunk-error");

        when(binanceApiManager.getTickerPrice24("FOO")).thenThrow(new RuntimeException("API failed"));

        assertFalse(manager.shouldRetryFailedChunk(order));
    }

    @Test
    void testProcessExecutedProgressiveBuyChunks_triggersNextChunkOnlyWhenInRange() throws Exception {
        ChunkOrder eligibleChunk = new ChunkOrder();
        eligibleChunk.setChunkIndex(1);
        eligibleChunk.setTotalChunkCount(3);
        eligibleChunk.setSymbol("BTCUSDT");
        eligibleChunk.setId("ok");
        TradingSignal signal = new TradingSignal();
        signal.setEntryStart("100");
        signal.setEntryEnd("110");
        eligibleChunk.setTradingSignal(signal);

        TickerData ticker = new TickerData();
        ticker.setLastPrice("105");
        when(binanceApiManager.getTickerPrice24("BTCUSDT")).thenReturn(ticker);

        when(botOrderService.getProgressiveChunksByStatusesAndDate(eq(List.of(OrderStatus.BUY_EXECUTED)), any()))
                .thenReturn(List.of(eligibleChunk));
        when(binanceHelper.getSearchDate()).thenReturn(new Date());

        manager.processExecutedProgressiveBuyChunks();

        verify(publisher).publishEvent(argThat(e -> e instanceof NewProgressiveChunkedSellOrderEvent));
    }

    @Test
    void testProcessFailedProgressiveBuyChunks_triggersRetryOnlyWhenInRange() throws Exception {
        ChunkOrder failedChunk = new ChunkOrder();
        failedChunk.setSymbol("ADAUSDT");
        failedChunk.setId("retry");
        TradingSignal signal = new TradingSignal();
        signal.setEntryStart("0.25");
        signal.setEntryEnd("0.35");
        failedChunk.setTradingSignal(signal);

        TickerData ticker = new TickerData();
        ticker.setLastPrice("0.30");
        when(binanceApiManager.getTickerPrice24("ADAUSDT")).thenReturn(ticker);
        when(binanceHelper.getSearchDate()).thenReturn(new Date());
        when(botOrderService.getProgressiveChunksByStatusesAndDate(eq(List.of(OrderStatus.BUY_FAILED)), any()))
                .thenReturn(List.of(failedChunk));

        manager.processFailedProgressiveBuyChunks();

        verify(publisher).publishEvent(argThat(e -> e instanceof NewProgressiveChunkedSellOrderEvent));
    }

    @Test
    void testPlaceNextChunk_shouldSetExecutedStatusAndPublishEvents() throws Exception {
        // Arrange
        TradingSignal signal = new TradingSignal();
        signal.setSymbol("BTCUSDT");
        signal.setStopLoss("29000");
        signal.setEntryStart("29500");
        signal.setEntryEnd("30000");
        signal.setTakeProfits(List.of("31000", "32000", "33000"));

        double currentPrice = 29500;
        int chunkIndex = 1;
        int maxChunks = 3;

        when(botConfig.getAmount()).thenReturn(900.0);

        ArgumentCaptor<ChunkOrder> savedOrderCaptor = ArgumentCaptor.forClass(ChunkOrder.class);
        when(botOrderService.saveChunkOrder(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        manager.placeNextChunk(signal, currentPrice, chunkIndex, maxChunks);

        // Assert
        verify(binanceApiManager).newOrder(eq("BTCUSDT"), eq(currentPrice), anyDouble());
        verify(botOrderService).saveChunkOrder(savedOrderCaptor.capture());
        ChunkOrder savedOrder = savedOrderCaptor.getValue();

        assertEquals(OrderStatus.BUY_EXECUTED, savedOrder.getStatus());
        assertEquals("BTCUSDT", savedOrder.getSymbol());
        assertEquals(chunkIndex, savedOrder.getChunkIndex());
        assertEquals(maxChunks, savedOrder.getTotalChunkCount());
        assertEquals(currentPrice, savedOrder.getEntryPoint());
        assertEquals(33.0, savedOrder.getTakeProfitAllocation(), 0.0001);
        assertEquals(1, savedOrder.getTakeProfitIndex());

        verify(publisher).publishEvent(argThat(e -> e instanceof InfoEvent));
        verify(publisher).publishEvent(argThat(e -> e instanceof NewProgressiveChunkedSellOrderEvent));
    }

    @Test
    void testPlaceNextChunk_shouldSetFailedStatusAndPublishErrorEvent_onOrderFailure() throws Exception {
        // Arrange
        TradingSignal signal = new TradingSignal();
        signal.setSymbol("BTCUSDT");
        signal.setStopLoss("29000");
        signal.setEntryStart("29500");
        signal.setEntryEnd("30000");
        signal.setTakeProfits(List.of("31000"));

        double currentPrice = 29500;
        int chunkIndex = 0;
        int maxChunks = 1;

        when(botConfig.getAmount()).thenReturn(500.0);
        doThrow(new RuntimeException("Order failed")).when(binanceApiManager).newOrder(any(), anyDouble(), anyDouble());

        when(botOrderService.saveChunkOrder(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        manager.placeNextChunk(signal, currentPrice, chunkIndex, maxChunks);

        // Assert
        verify(botOrderService).saveChunkOrder(argThat(order ->
                order.getStatus() == OrderStatus.BUY_FAILED &&
                        order.getChunkIndex() == 0 &&
                        order.getSymbol().equals("BTCUSDT")
        ));

        verify(publisher).publishEvent(argThat(e -> e instanceof ErrorEvent));
        verify(publisher).publishEvent(argThat(e -> e instanceof NewProgressiveChunkedSellOrderEvent));
    }

    @Test
    void shouldPlaceNextChunk_whenPriceInEntryRangeAndBullish_returnsTrue() {
        TradingSignal signal = new TradingSignal();
        signal.setEntryStart("1.10");
        signal.setEntryEnd("1.00");

        double currentPrice = 1.05;
        List<KlineData> klines = Collections.emptyList();

        when(binanceHelper.isShortTermBullish(klines)).thenReturn(true);

        boolean result = manager.shouldPlaceNextChunk(signal, currentPrice, klines);

        assertTrue(result);
    }

    @Test
    void shouldPlaceNextChunk_whenPriceNotInEntryRange_returnsFalse() {
        TradingSignal signal = new TradingSignal();
        signal.setEntryStart("1.10");
        signal.setEntryEnd("1.00");

        double currentPrice = 1.20;
        List<KlineData> klines = Collections.emptyList();

        when(binanceHelper.isShortTermBullish(klines)).thenReturn(true);

        boolean result = manager.shouldPlaceNextChunk(signal, currentPrice, klines);

        assertFalse(result);
    }

    @Test
    void shouldPlaceNextChunk_whenNotBullish_returnsFalse() {
        TradingSignal signal = new TradingSignal();
        signal.setEntryStart("1.10");
        signal.setEntryEnd("1.00");

        double currentPrice = 1.05;
        List<KlineData> klines = Collections.emptyList();

        when(binanceHelper.isShortTermBullish(klines)).thenReturn(false);

        boolean result = manager.shouldPlaceNextChunk(signal, currentPrice, klines);

        assertFalse(result);
    }

    @Test
    void shouldPlaceNextChunk_whenPriceOutOfBoundsAndNotBullish_returnsFalse() {
        TradingSignal signal = new TradingSignal();
        signal.setEntryStart("1.10");
        signal.setEntryEnd("1.00");

        double currentPrice = 0.95;
        List<KlineData> klines = Collections.emptyList();

        when(binanceHelper.isShortTermBullish(klines)).thenReturn(false);

        boolean result = manager.shouldPlaceNextChunk(signal, currentPrice, klines);

        assertFalse(result);
    }

    @Test
    void shouldTriggerNextChunk_returnsFalse_ifLastChunk() {
        ChunkOrder chunk = new ChunkOrder();
        chunk.setChunkIndex(2);
        chunk.setTotalChunkCount(3);
        chunk.setId("chunk-id");

        boolean result = manager.shouldTriggerNextChunk(chunk);
        assertFalse(result);
    }

    @Test
    void shouldTriggerNextChunk_returnsTrue_ifInEntryZone() throws Exception {
        ChunkOrder chunk = new ChunkOrder();
        chunk.setChunkIndex(1);
        chunk.setTotalChunkCount(3);
        chunk.setSymbol("BTCUSDT");
        TradingSignal signal = new TradingSignal();
        signal.setEntryStart("90");
        signal.setEntryEnd("110");
        chunk.setTradingSignal(signal);

        TickerData ticker = new TickerData();
        ticker.setLastPrice("100");
        when(binanceApiManager.getTickerPrice24("BTCUSDT")).thenReturn(ticker);

        boolean result = manager.shouldTriggerNextChunk(chunk);
        assertTrue(result);
    }

    @Test
    void shouldRetryFailedChunk_returnsTrue_ifPriceInRange() throws Exception {
        ChunkOrder chunk = new ChunkOrder();
        chunk.setSymbol("BTCUSDT");
        chunk.setId("chunk-1");

        TradingSignal signal = new TradingSignal();
        signal.setEntryStart("90");
        signal.setEntryEnd("110");
        chunk.setTradingSignal(signal);

        TickerData ticker = new TickerData();
        ticker.setLastPrice("95");
        when(binanceApiManager.getTickerPrice24("BTCUSDT")).thenReturn(ticker);

        boolean result = manager.shouldRetryFailedChunk(chunk);
        assertTrue(result);
    }

    @Test
    void shouldRetryFailedChunk_returnsFalse_ifException() throws Exception {
        ChunkOrder chunk = new ChunkOrder();
        chunk.setSymbol("INVALID");
        chunk.setId("chunk-err");
        TradingSignal signal = new TradingSignal();
        signal.setEntryStart("90");
        signal.setEntryEnd("110");
        chunk.setTradingSignal(signal);

        when(binanceApiManager.getTickerPrice24("INVALID")).thenThrow(new RuntimeException("Fail"));

        boolean result = manager.shouldRetryFailedChunk(chunk);
        assertFalse(result);
    }

    @Test
    void shouldPlaceNextChunk_returnsTrue_ifShortTermBullishAndInRange() {
        TradingSignal signal = new TradingSignal();
        signal.setEntryStart("95");
        signal.setEntryEnd("105");

        List<KlineData> klines = List.of(new KlineData());
        when(binanceHelper.isShortTermBullish(klines)).thenReturn(true);

        boolean result = manager.shouldPlaceNextChunk(signal, 100, klines);
        assertTrue(result);
    }

    @Test
    void shouldPlaceNextChunk_returnsFalse_ifNotBullish() {
        TradingSignal signal = new TradingSignal();
        signal.setEntryStart("95");
        signal.setEntryEnd("105");

        List<KlineData> klines = List.of(new KlineData());
        when(binanceHelper.isShortTermBullish(klines)).thenReturn(false);

        boolean result = manager.shouldPlaceNextChunk(signal, 100, klines);
        assertFalse(result);
    }

    @Test
    void testHandleProgressiveChunkedBuy_shouldPlaceNextChunk() throws Exception {
        NewProgressiveChunkedBuyOrderEvent event = new NewProgressiveChunkedBuyOrderEvent(this, signal, tickerData);

        List<KlineData> klines = List.of(createKline(100000), createKline(110000));
        when(appConfig.getMinQuoteVolume()).thenReturn(90000.0);
        when(binanceApiManager.getListOfKlineData("BTCUSDT", IntervalType.FIVE_MINUTES)).thenReturn(klines);
        when(binanceHelper.calculateDynamicChunkCount(anyDouble())).thenReturn(5);
        when(botOrderService.getChunksBySignalAndStatuses(eq("signal123"), any())).thenReturn(List.of());
        when(binanceHelper.isShortTermBullish(klines)).thenReturn(true);

        manager.handleProgressiveChunkedBuy(event);

        // Verify chunk is placed
        verify(binanceHelper).calculateDynamicChunkCount(anyDouble());
    }

    @Test
    void testHandleProgressiveChunkedBuy_shouldSkipDueToLowVolume() throws Exception {
        NewProgressiveChunkedBuyOrderEvent event = new NewProgressiveChunkedBuyOrderEvent(this, signal, tickerData);

        List<KlineData> klines = List.of(createKline(1000), createKline(1200));
        when(appConfig.getMinQuoteVolume()).thenReturn(90000.0);
        when(binanceApiManager.getListOfKlineData(any(), any())).thenReturn(klines);

        manager.handleProgressiveChunkedBuy(event);

        verify(binanceHelper, never()).calculateDynamicChunkCount(anyDouble());
    }

    @Test
    void testHandleProgressiveChunkedBuy_shouldSkipWhenChunksFull() throws Exception {
        NewProgressiveChunkedBuyOrderEvent event = new NewProgressiveChunkedBuyOrderEvent(this, signal, tickerData);

        List<KlineData> klines = List.of(createKline(100000));
        when(appConfig.getMinQuoteVolume()).thenReturn(50000.0);
        when(binanceApiManager.getListOfKlineData(any(), any())).thenReturn(klines);
        when(binanceHelper.calculateDynamicChunkCount(anyDouble())).thenReturn(2);
        when(botOrderService.getChunksBySignalAndStatuses(eq("signal123"), any())).thenReturn(
                List.of(new ChunkOrder(), new ChunkOrder()));

        manager.handleProgressiveChunkedBuy(event);

        verify(binanceHelper, never()).isShortTermBullish(any());
    }

    @Test
    void testHandleProgressiveChunkedBuy_shouldTriggerErrorEvent() throws Exception {
        NewProgressiveChunkedBuyOrderEvent event = new NewProgressiveChunkedBuyOrderEvent(this, signal, tickerData);

        when(binanceApiManager.getListOfKlineData(any(), any())).thenThrow(new RuntimeException("API error"));

        manager.handleProgressiveChunkedBuy(event);

        verify(publisher).publishEvent(errorEventCaptor.capture());
    }

    private KlineData createKline(double quoteVolume) {
        KlineData kline = new KlineData();
        kline.setQuoteAssetVolume(quoteVolume);
        return kline;
    }
}
