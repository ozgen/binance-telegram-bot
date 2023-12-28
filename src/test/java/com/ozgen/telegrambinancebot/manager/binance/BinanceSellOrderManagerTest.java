package com.ozgen.telegrambinancebot.manager.binance;


import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.model.binance.OrderResponse;
import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.bot.FutureTrade;
import com.ozgen.telegrambinancebot.model.bot.SellOrder;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.FutureTradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BinanceSellOrderManagerTest {

    private static final String SYMBOL = "BNBBTC";
    private static final Double BUY_PRICE = 0.01;
    private static final String START_ENTRY = "0.009";
    private static final String END_ENTRY = "0.011";

    private static final String STOPLOSS = "0.008";
    private static final String CURRENCY_RATE = "BTCUSD";
    private static final Double AMOUNT = 100.0;
    private static final Double PERCENTAGE_INC = 0.5;
    private static final Double PROFIT_PERCENTAGE = 6.0;
    private static final String CURRENCY = "BTC";
    private static final String BNB = "BNB";
    private static final Double BNB_AMOUNT = 0.1;
    private static final Double BNB_TOTAL_AMOUNT = 0.23;

    @Mock
    private BinanceApiManager binanceApiManager;
    @Mock
    private BotConfiguration botConfiguration;
    @Mock
    private FutureTradeService futureTradeService;
    @Mock
    private BotOrderService botOrderService;
    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private BinanceSellOrderManager binanceSellOrderManager;

    @Mock
    private BuyOrder buyOrder;
    @Mock
    private TradingSignal tradingSignal;
    @Mock
    private SnapshotData snapshotData;
    @Mock
    private OrderResponse orderResponse;
    @Mock
    private FutureTrade futureTrade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(this.tradingSignal.getSymbol())
                .thenReturn(SYMBOL);
        when(this.tradingSignal.getEntryStart())
                .thenReturn(START_ENTRY);
        when(this.tradingSignal.getEntryEnd())
                .thenReturn(END_ENTRY);
        when(this.tradingSignal.getStopLoss())
                .thenReturn(STOPLOSS);

        when(this.botConfiguration.getCurrency())
                .thenReturn(CURRENCY);
        when(this.botConfiguration.getAmount())
                .thenReturn(AMOUNT);
        when(this.botConfiguration.getCurrencyRate())
                .thenReturn(CURRENCY_RATE);
        when(this.botConfiguration.getPercentageInc())
                .thenReturn(PERCENTAGE_INC);
        when(this.botConfiguration.getProfitPercentage())
                .thenReturn(PROFIT_PERCENTAGE);

        when(this.buyOrder.getSymbol())
                .thenReturn(SYMBOL);
        when(this.buyOrder.getBuyPrice())
                .thenReturn(BUY_PRICE);
        when(this.buyOrder.getCoinAmount())
                .thenReturn(BNB_TOTAL_AMOUNT);
        when(this.buyOrder.getStopLoss())
                .thenReturn(0.012);
        when(this.buyOrder.getStopLossLimit())
                .thenReturn(0.13);
        when(this.buyOrder.getTradingSignal())
                .thenReturn(this.tradingSignal);
    }

    @Test
    void testProcessNewSellOrderEvent_Success() throws Exception {
        // Arrange
        when(this.binanceApiManager.getAccountSnapshot())
                .thenReturn(this.snapshotData);
        when(this.snapshotData.getCoinValue(BNB))
                .thenReturn(BNB_AMOUNT);
        when(this.botOrderService.getSellOrder(this.tradingSignal))
                .thenReturn(Optional.empty());
        when(this.botOrderService.createSellOrder(any(SellOrder.class)))
                .thenAnswer((Answer<SellOrder>) invocation -> (SellOrder) invocation.getArguments()[0]);
        when(this.binanceApiManager.newOrderWithStopLoss(any(), any(), any(), any(), any()))
                .thenReturn(this.orderResponse);
        NewSellOrderEvent event = new NewSellOrderEvent(this, this.buyOrder);

        //Act
        this.binanceSellOrderManager.processNewSellOrderEvent(event);

        // Assert
        double expectedSellPrice = (BUY_PRICE * (PROFIT_PERCENTAGE / 100) + BUY_PRICE);
        double expectedStopLoss = (Double.parseDouble(STOPLOSS) - (Double.parseDouble(STOPLOSS) * (PERCENTAGE_INC / 100)));
        verify(this.botOrderService)
                .getSellOrder(this.tradingSignal);
        ArgumentCaptor<SellOrder> sellOrderCaptor = ArgumentCaptor.forClass(SellOrder.class);
        verify(this.botOrderService)
                .createSellOrder(sellOrderCaptor.capture());
        SellOrder sellOrder = sellOrderCaptor.getValue();
        assertEquals(BNB_AMOUNT, sellOrder.getCoinAmount());
        assertEquals(SYMBOL, sellOrder.getSymbol());
        assertEquals(expectedSellPrice, sellOrder.getSellPrice());
        assertEquals(expectedStopLoss, sellOrder.getStopLoss());
        assertEquals(Double.parseDouble(STOPLOSS), sellOrder.getStopLossLimit());
        assertEquals(this.tradingSignal, sellOrder.getTradingSignal());
        verify(this.binanceApiManager)
                .newOrderWithStopLoss(SYMBOL, sellOrder.getSellPrice(), sellOrder.getCoinAmount(), sellOrder.getStopLoss(), sellOrder.getStopLossLimit());
    }

    @Test
    void testProcessNewSellOrderEvent_Success_withTotal() throws Exception {
        // Arrange
        when(this.binanceApiManager.getAccountSnapshot())
                .thenReturn(this.snapshotData);
        when(this.snapshotData.getCoinValue(BNB))
                .thenReturn(BNB_TOTAL_AMOUNT);
        when(this.botOrderService.getSellOrder(this.tradingSignal))
                .thenReturn(Optional.empty());
        when(this.botOrderService.createSellOrder(any(SellOrder.class)))
                .thenAnswer((Answer<SellOrder>) invocation -> (SellOrder) invocation.getArguments()[0]);
        when(this.binanceApiManager.newOrderWithStopLoss(any(), any(), any(), any(), any()))
                .thenReturn(this.orderResponse);
        NewSellOrderEvent event = new NewSellOrderEvent(this, this.buyOrder);

        //Act
        this.binanceSellOrderManager.processNewSellOrderEvent(event);

        // Assert
        double expectedSellPrice = (BUY_PRICE * (PROFIT_PERCENTAGE / 100) + BUY_PRICE);
        double expectedStopLoss = (Double.parseDouble(STOPLOSS) - (Double.parseDouble(STOPLOSS) * (PERCENTAGE_INC / 100)));
        verify(this.botOrderService)
                .getSellOrder(this.tradingSignal);
        ArgumentCaptor<SellOrder> sellOrderCaptor = ArgumentCaptor.forClass(SellOrder.class);
        verify(this.botOrderService)
                .createSellOrder(sellOrderCaptor.capture());
        SellOrder sellOrder = sellOrderCaptor.getValue();
        assertEquals(BNB_TOTAL_AMOUNT, sellOrder.getCoinAmount());
        assertEquals(SYMBOL, sellOrder.getSymbol());
        assertEquals(expectedSellPrice, sellOrder.getSellPrice());
        assertEquals(expectedStopLoss, sellOrder.getStopLoss());
        assertEquals(Double.parseDouble(STOPLOSS), sellOrder.getStopLossLimit());
        assertEquals(this.tradingSignal, sellOrder.getTradingSignal());
        verify(this.binanceApiManager)
                .newOrderWithStopLoss(SYMBOL, sellOrder.getSellPrice(), sellOrder.getCoinAmount(), sellOrder.getStopLoss(), sellOrder.getStopLossLimit());
    }

    @Test
    void testProcessNewSellOrderEvent_with0CoinAmount() throws Exception {
        // Arrange
        when(this.binanceApiManager.getAccountSnapshot())
                .thenReturn(this.snapshotData);
        when(this.snapshotData.getCoinValue(BNB))
                .thenReturn(0.0);
        NewSellOrderEvent event = new NewSellOrderEvent(this, this.buyOrder);

        //Act
        this.binanceSellOrderManager.processNewSellOrderEvent(event);

        // Assert
        verify(this.botOrderService, never())
                .getSellOrder(this.tradingSignal);
        verify(this.botOrderService, never())
                .createSellOrder(any());
        verify(this.binanceApiManager, never())
                .newOrderWithStopLoss(any(), any(), any(), any(), any());
    }

    @Test
    void testProcessNewSellOrderEvent_withSnapshotApiException() throws Exception {
        // Arrange
        when(this.binanceApiManager.getAccountSnapshot())
                .thenThrow(RuntimeException.class);

        NewSellOrderEvent event = new NewSellOrderEvent(this, this.buyOrder);

        //Act
        this.binanceSellOrderManager.processNewSellOrderEvent(event);

        // Assert
        verify(this.botOrderService, never())
                .getSellOrder(this.tradingSignal);
        verify(this.botOrderService, never())
                .createSellOrder(any());
        verify(this.binanceApiManager, never())
                .newOrderWithStopLoss(any(), any(), any(), any(), any());
        verify(this.publisher)
                .publishEvent(any(ErrorEvent.class));
    }

    @Test
    void testProcessNewSellOrderEvent_withWrongSymbol() throws Exception {
        // Arrange
        when(this.buyOrder.getSymbol())
                .thenReturn("BNBBT");
        when(this.binanceApiManager.getAccountSnapshot())
                .thenReturn(this.snapshotData);
        NewSellOrderEvent event = new NewSellOrderEvent(this, this.buyOrder);

        //Act
        this.binanceSellOrderManager.processNewSellOrderEvent(event);

        // Assert
        verify(this.botOrderService, never())
                .getSellOrder(this.tradingSignal);
        verify(this.botOrderService, never())
                .createSellOrder(any());
        verify(this.binanceApiManager, never())
                .newOrderWithStopLoss(any(), any(), any(), any(), any());
    }

    @Test
    void testProcessNewSellOrderEvent_withNewOrderWithStopLossApiException() throws Exception {
        // Arrange
        when(this.binanceApiManager.getAccountSnapshot())
                .thenReturn(this.snapshotData);
        when(this.snapshotData.getCoinValue(BNB))
                .thenReturn(BNB_AMOUNT);
        when(this.botOrderService.getSellOrder(this.tradingSignal))
                .thenReturn(Optional.empty());
        when(this.futureTradeService.createFutureTrade(this.tradingSignal, TradeStatus.ERROR_SELL))
                .thenReturn(this.futureTrade);
        when(this.binanceApiManager.newOrderWithStopLoss(any(), any(), any(), any(), any()))
                .thenThrow(RuntimeException.class);
        NewSellOrderEvent event = new NewSellOrderEvent(this, this.buyOrder);

        //Act
        this.binanceSellOrderManager.processNewSellOrderEvent(event);

        // Assert
        verify(this.botOrderService)
                .getSellOrder(this.tradingSignal);
        verify(this.binanceApiManager)
                .newOrderWithStopLoss(any(), any(), any(), any(), any());
        verify(this.futureTradeService)
                .createFutureTrade(this.tradingSignal, TradeStatus.ERROR_SELL);
        verify(this.botOrderService, never())
                .createSellOrder(any());
        verify(this.publisher)
                .publishEvent(any(ErrorEvent.class));
    }
}
