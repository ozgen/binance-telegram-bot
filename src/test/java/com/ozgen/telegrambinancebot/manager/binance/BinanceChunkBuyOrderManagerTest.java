package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.AppConfiguration;
import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.model.binance.AssetBalance;
import com.ozgen.telegrambinancebot.model.binance.KlineData;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.bot.ChunkOrder;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.InfoEvent;
import com.ozgen.telegrambinancebot.model.events.NewChunkedBuyExecutionEvent;
import com.ozgen.telegrambinancebot.model.events.NewSellChunkOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BinanceChunkBuyOrderManagerTest {

    @Mock
    private BinanceApiManager binanceApiManager;
    @Mock
    private BotOrderService botOrderService;
    @Mock
    private BinanceHelper binanceHelper;
    @Mock
    private ApplicationEventPublisher publisher;
    @Mock
    private BotConfiguration botConfiguration;
    @Mock
    private AppConfiguration appConfiguration;
    @Mock
    private TradingSignalService tradingSignalService;

    @InjectMocks
    private BinanceChunkBuyOrderManager manager;



    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleChunkedBuyEvent_success() throws Exception {
        TickerData ticker = new TickerData();
        ticker.setLastPrice("100");

        TradingSignal signal = new TradingSignal();
        signal.setSymbol("BTCUSDT");
        signal.setEntryEnd("90");

        NewChunkedBuyExecutionEvent event = new NewChunkedBuyExecutionEvent(this, signal, ticker);

        when(binanceHelper.getUserAssets()).thenReturn(List.of(mock(AssetBalance.class)));
        when(binanceHelper.hasAccountEnoughAsset(any(), eq(signal))).thenReturn(true);
        when(botConfiguration.getAmount()).thenReturn(100.0);
        when(botConfiguration.getPercentageInc()).thenReturn(1.0);
        when(binanceApiManager.getListOfKlineData(any(), any())).thenReturn(List.of(
                mockKline(1000.0), mockKline(2000.0)
        ));
        when(appConfiguration.getMinQuoteVolume()).thenReturn(500.0);
        when(botOrderService.saveChunkOrder(any())).thenAnswer(i -> i.getArgument(0));

        manager.handleChunkedBuyEvent(event);

        verify(botOrderService, atLeastOnce()).saveChunkOrder(any());
        verify(publisher, atLeastOnce()).publishEvent(any(InfoEvent.class));
        verify(tradingSignalService).updateTradingSignal(signal);
        assertEquals(ProcessStatus.DONE, signal.getIsProcessed());
    }

    @Test
    void testHandleChunkedBuyEvent_insufficientFunds() throws Exception {
        TickerData ticker = new TickerData();
        ticker.setLastPrice("100");

        TradingSignal signal = new TradingSignal();
        signal.setSymbol("BTCUSDT");

        NewChunkedBuyExecutionEvent event = new NewChunkedBuyExecutionEvent(this, signal, ticker);

        when(binanceHelper.getUserAssets()).thenReturn(Collections.emptyList());
        when(binanceHelper.hasAccountEnoughAsset(any(), any())).thenReturn(false);

        manager.handleChunkedBuyEvent(event);

        verify(botOrderService, never()).saveChunkOrder(any());
    }

    @Test
    void testHandleChunkOrder_exception() throws Exception {
        ChunkOrder chunkOrder = new ChunkOrder();
        chunkOrder.setSymbol("BTCUSDT");

        when(binanceApiManager.getTickerPrice24("BTCUSDT")).thenThrow(new RuntimeException("Failed"));

        manager.handleChunkOrder(chunkOrder);

        verify(publisher).publishEvent(any(ErrorEvent.class));
    }

    @Test
    void testProcessExecutedBuyChunkOrders() {
        ChunkOrder chunk = new ChunkOrder();
        when(binanceHelper.getSearchDate()).thenReturn(new Date());
        when(botOrderService.getBuyChunksByStatusesAndDate(anyList(), any())).thenReturn(List.of(chunk));

        manager.processExecutedBuyChunkOrders();

        verify(publisher).publishEvent(any(NewSellChunkOrderEvent.class));
    }

    @Test
    void testExecuteBuyChunks_lowLiquidity() throws Exception {
        TradingSignal signal = new TradingSignal();
        signal.setSymbol("BTCUSDT");

        TickerData ticker = new TickerData();
        ticker.setLastPrice("100");

        when(binanceHelper.getUserAssets()).thenReturn(List.of(mock(AssetBalance.class)));
        when(binanceHelper.hasAccountEnoughAsset(any(), eq(signal))).thenReturn(true);
        when(binanceApiManager.getListOfKlineData(any(), any())).thenReturn(List.of(mockKline(10.0)));
        when(appConfiguration.getMinQuoteVolume()).thenReturn(500.0);

        manager.handleChunkedBuyEvent(new NewChunkedBuyExecutionEvent(this, signal, ticker));

        verify(botOrderService, never()).saveChunkOrder(any());
    }


    private KlineData mockKline(double volume) {
        KlineData kline = new KlineData();
        kline.setQuoteAssetVolume(volume);
        return kline;
    }
}
