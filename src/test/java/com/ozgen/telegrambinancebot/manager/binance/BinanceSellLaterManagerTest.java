package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.ScheduleConfiguration;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BinanceSellLaterManagerTest {

    @Mock
    private TradingSignalService tradingSignalService;

    @Mock
    private BotOrderService botOrderService;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private ScheduleConfiguration scheduleConfiguration;

    @Mock
    private BinanceHelper binanceHelper;

    @Mock
    private BinanceApiManager binanceApiManager;

    @InjectMocks
    private BinanceSellLaterManager binanceSellLaterManager;

    @Captor
    private ArgumentCaptor<NewSellOrderEvent> sellOrderEventCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessSellLaterOrders_NoTradingSignals() {
        when(tradingSignalService.getSellLaterTradingSignalsAfterDateAndIsProcessIn(any(Date.class), anyList()))
                .thenReturn(Collections.emptyList());

        binanceSellLaterManager.processSellLaterOrders();

        verify(botOrderService, never()).getBuyOrders(anyList());
    }

    @Test
    void testProcessSellLaterOrders_ValidSellOrder() throws Exception {
        // Mock data
        TradingSignal tradingSignal = new TradingSignal();
        BuyOrder buyOrder = new BuyOrder();
        buyOrder.setSymbol("BTCUSDT");
        buyOrder.setBuyPrice(50000.0);
        buyOrder.setId("test_sell_id");
        TickerData tickerData = new TickerData();
        tickerData.setLastPrice("55000.0"); // Price is higher, so it's valid for selling

        // Mock dependencies
        when(tradingSignalService.getSellLaterTradingSignalsAfterDateAndIsProcessIn(any(Date.class), anyList()))
                .thenReturn(List.of(tradingSignal));
        when(botOrderService.getBuyOrders(anyList()))
                .thenReturn(List.of(buyOrder));
        when(binanceApiManager.getTickerPrice24(anyString()))
                .thenReturn(tickerData);
        when(binanceHelper.isCoinPriceAvailableToSell(buyOrder.getBuyPrice(), tickerData))
                .thenReturn(true);

        binanceSellLaterManager.processSellLaterOrders();

        // Capture event and verify publish event was triggered
        verify(publisher).publishEvent(sellOrderEventCaptor.capture());
        NewSellOrderEvent event = sellOrderEventCaptor.getValue();
        assertEquals(buyOrder, event.getBuyOrder());
    }

    @Test
    void testProcessSellLaterOrders_CoinPriceNotAvailableToSell() throws Exception {
        // Mock data
        TradingSignal tradingSignal = new TradingSignal();
        BuyOrder buyOrder = new BuyOrder();
        buyOrder.setSymbol("BTCUSDT");
        buyOrder.setBuyPrice(50000.0);
        buyOrder.setId("test_sell_id");
        TickerData tickerData = new TickerData();
        tickerData.setLastPrice("48000.0"); // Price is lower, so not available for selling

        // Mock dependencies
        when(tradingSignalService.getSellLaterTradingSignalsAfterDateAndIsProcessIn(any(Date.class), anyList()))
                .thenReturn(List.of(tradingSignal));
        when(botOrderService.getBuyOrders(anyList()))
                .thenReturn(List.of(buyOrder));
        when(binanceApiManager.getTickerPrice24(anyString()))
                .thenReturn(tickerData);
        when(binanceHelper.isCoinPriceAvailableToSell(buyOrder.getBuyPrice(), tickerData))
                .thenReturn(false);

        binanceSellLaterManager.processSellLaterOrders();

        // Verify that no event is published
        verify(publisher, never()).publishEvent(any(NewSellOrderEvent.class));
    }

    @Test
    void testProcessSellLaterOrders_TickerPriceNull() throws Exception {
        // Mock data
        TradingSignal tradingSignal = new TradingSignal();
        BuyOrder buyOrder = new BuyOrder();
        buyOrder.setSymbol("BTCUSDT");
        buyOrder.setId("test_sell_id");

        // Mock dependencies
        when(tradingSignalService.getSellLaterTradingSignalsAfterDateAndIsProcessIn(any(Date.class), anyList()))
                .thenReturn(List.of(tradingSignal));
        when(botOrderService.getBuyOrders(anyList()))
                .thenReturn(List.of(buyOrder));
        when(binanceApiManager.getTickerPrice24(anyString()))
                .thenReturn(null);

        binanceSellLaterManager.processSellLaterOrders();

        // Verify that no event is published
        verify(publisher, never()).publishEvent(any(NewSellOrderEvent.class));
    }

    @Test
    void testProcessSellLaterOrders_ExceptionHandling() throws Exception {
        // Mock data
        TradingSignal tradingSignal = new TradingSignal();
        BuyOrder buyOrder = new BuyOrder();
        buyOrder.setSymbol("BTCUSDT");
        buyOrder.setId("test_id");

        // Mock dependencies
        when(tradingSignalService.getSellLaterTradingSignalsAfterDateAndIsProcessIn(any(Date.class), anyList()))
                .thenReturn(List.of(tradingSignal));
        when(botOrderService.getBuyOrders(anyList()))
                .thenReturn(List.of(buyOrder));
        when(binanceApiManager.getTickerPrice24(anyString()))
                .thenThrow(new RuntimeException("API error"));

        assertThatExceptionOfType(RuntimeException.class)
        .isThrownBy(() -> binanceSellLaterManager.processSellLaterOrders());

        // Verify that no event is published
        verify(publisher, never()).publishEvent(any(NewSellOrderEvent.class));
    }
}
