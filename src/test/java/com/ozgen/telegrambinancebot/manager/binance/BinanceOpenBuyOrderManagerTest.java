package com.ozgen.telegrambinancebot.manager.binance;


import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.configuration.properties.ScheduleConfiguration;
import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.model.binance.CancelAndNewOrderResponse;
import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.bot.FutureTrade;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.FutureTradeService;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BinanceOpenBuyOrderManagerTest {

    private static final String SYMBOL = "BNBBTC";
    private static final String LAST_PRICE = "0.01";
    private static final String LAST_PRICE_OUT_RANGE = "0.02";
    private static final String START_ENTRY = "0.009";
    private static final String END_ENTRY = "0.011";
    private static final String CURRENCY_RATE = "BTCUSD";
    private static final Double AMOUNT = 100.0;
    private static final Double PERCENTAGE_INC = 0.5;
    private static final Double PROFIT_PERCENTAGE = 6.0;
    private static final String CURRENCY = "BTC";
    private static final String STOPLOSS = "0.008";
    private static final Long ORDER_INFO_ID = 1L;
    private static final String BNB = "BNB";
    private static final String BNB_AMOUNT = "0.1";
    private static final String BNB_TOTAL_AMOUNT = "0.23";
    private static final Double BNB_TOTAL_AMOUNT_D = 0.23;
    private static final Double BUY_PRICE = 0.01;

    @Mock
    private BinanceApiManager binanceApiManager;
    @Mock
    private TradingSignalService tradingSignalService;
    @Mock
    private FutureTradeService futureTradeService;
    @Mock
    private BotOrderService botOrderService;
    @Mock
    private ApplicationEventPublisher publisher;
    @Mock
    private ScheduleConfiguration scheduleConfiguration;
    @Mock
    private BotConfiguration botConfiguration;

    @InjectMocks
    private BinanceOpenBuyOrderManager binanceOpenBuyOrderManager;

    @Mock
    private TickerData tickerData;
    @Mock
    private TradingSignal tradingSignal;
    @Mock
    private CancelAndNewOrderResponse cancelAndNewOrderResponse;
    @Mock
    private OrderInfo orderInfo;
    @Mock
    private FutureTrade futureTrade;

    private BuyOrder buyOrder;

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

        when(this.scheduleConfiguration.getMonthBefore())
                .thenReturn(1);

        when(this.orderInfo.getOrderId())
                .thenReturn(ORDER_INFO_ID);
        when(this.orderInfo.getOrigQty())
                .thenReturn(BNB_TOTAL_AMOUNT);
        when(this.orderInfo.getExecutedQty())
                .thenReturn(BNB_AMOUNT);

        this.buyOrder = new BuyOrder();
        this.buyOrder.setSymbol(SYMBOL);
        this.buyOrder.setBuyPrice(BUY_PRICE);
        this.buyOrder.setCoinAmount(BNB_TOTAL_AMOUNT_D);
        this.buyOrder.setStopLoss(0.012);
        this.buyOrder.setStopLossLimit(0.013);
        this.buyOrder.setTradingSignal(this.tradingSignal);

        when(this.tickerData.getLastPrice())
                .thenReturn(LAST_PRICE);
    }

    @Test
    public void testProcessOpenBuyOrders_Success() throws Exception {
        // Arrange
        when(this.tradingSignalService.getTradingSignalsAfterDateAndIsProcessIn(any(), anyList()))
                .thenReturn(List.of(this.tradingSignal));
        when(this.binanceApiManager.getOpenOrders(SYMBOL))
                .thenReturn(List.of(this.orderInfo));
        when(this.binanceApiManager.getTickerPrice24(SYMBOL))
                .thenReturn(this.tickerData);
        when(this.botOrderService.getBuyOrder(this.tradingSignal))
                .thenReturn(Optional.of(this.buyOrder));
        when(this.botOrderService.createBuyOrder(any(BuyOrder.class)))
                .thenAnswer((Answer<BuyOrder>) invocation -> (BuyOrder) invocation.getArguments()[0]);
        when(this.binanceApiManager.cancelAndNewOrderWithStopLoss(any(), any(), any(), any(), any(), any()))
                .thenReturn(this.cancelAndNewOrderResponse);

        //Act
        this.binanceOpenBuyOrderManager.processOpenBuyOrders();

        // Assert
        verify(this.tradingSignalService)
                .getTradingSignalsAfterDateAndIsProcessIn(any(), anyList());
        verify(this.binanceApiManager)
                .getOpenOrders(SYMBOL);
        verify(this.binanceApiManager)
                .getTickerPrice24(SYMBOL);
        verify(this.botOrderService)
                .getBuyOrder(this.tradingSignal);
        ArgumentCaptor<BuyOrder> buyOrderCaptor = ArgumentCaptor.forClass(BuyOrder.class);
        verify(this.botOrderService)
                .createBuyOrder(buyOrderCaptor.capture());
        BuyOrder updatedBuyOrder = buyOrderCaptor.getValue();
        double expectedBuyPrice = (Double.parseDouble(LAST_PRICE) * (PERCENTAGE_INC / 100) + Double.parseDouble(LAST_PRICE));
        double expectedCoinAmount = Double.parseDouble(BNB_TOTAL_AMOUNT) - Double.parseDouble(BNB_AMOUNT);
        double expectedStopLoss = (Double.parseDouble(END_ENTRY) - (Double.parseDouble(END_ENTRY) * (PERCENTAGE_INC / 100)));
        assertEquals(expectedCoinAmount, updatedBuyOrder.getCoinAmount());
        assertEquals(expectedBuyPrice, updatedBuyOrder.getBuyPrice());
        assertEquals(expectedBuyPrice, updatedBuyOrder.getBuyPrice());
        assertEquals(expectedStopLoss, updatedBuyOrder.getStopLoss());
        assertEquals(Double.parseDouble(END_ENTRY), updatedBuyOrder.getStopLossLimit());
        verify(this.binanceApiManager)
                .cancelAndNewOrderWithStopLoss(SYMBOL, expectedBuyPrice, expectedCoinAmount, expectedStopLoss, Double.parseDouble(END_ENTRY), ORDER_INFO_ID);
        ArgumentCaptor<NewSellOrderEvent> eventCaptor = ArgumentCaptor.forClass(NewSellOrderEvent.class);
        verify(this.publisher)
                .publishEvent(eventCaptor.capture());
        NewSellOrderEvent newSellOrderEvent = eventCaptor.getValue();
        BuyOrder eventBuyOrder = newSellOrderEvent.getBuyOrder();
        assertEquals(updatedBuyOrder, eventBuyOrder);
    }

    @Test
    public void testProcessOpenBuyOrders_withTickerDataApiException() throws Exception {
        // Arrange
        when(this.tradingSignalService.getTradingSignalsAfterDateAndIsProcessIn(any(), anyList()))
                .thenReturn(List.of(this.tradingSignal));
        when(this.binanceApiManager.getOpenOrders(SYMBOL))
                .thenReturn(List.of(this.orderInfo));
        when(this.binanceApiManager.getTickerPrice24(SYMBOL))
                .thenThrow(RuntimeException.class);

        //Act
        this.binanceOpenBuyOrderManager.processOpenBuyOrders();

        // Assert
        verify(this.tradingSignalService)
                .getTradingSignalsAfterDateAndIsProcessIn(any(), anyList());
        verify(this.binanceApiManager)
                .getOpenOrders(SYMBOL);
        verify(this.binanceApiManager)
                .getTickerPrice24(SYMBOL);
        verify(this.botOrderService, never())
                .getBuyOrder(this.tradingSignal);
        verify(this.botOrderService, never())
                .createBuyOrder(any());
        verify(this.publisher, never())
                .publishEvent(any());
    }

    @Test
    public void testProcessOpenBuyOrders_withOpenOrdersApiException() throws Exception {
        // Arrange
        when(this.tradingSignalService.getTradingSignalsAfterDateAndIsProcessIn(any(), anyList()))
                .thenReturn(List.of(this.tradingSignal));
        when(this.binanceApiManager.getOpenOrders(SYMBOL))
                .thenThrow(RuntimeException.class);

        //Act
        this.binanceOpenBuyOrderManager.processOpenBuyOrders();

        // Assert
        verify(this.tradingSignalService)
                .getTradingSignalsAfterDateAndIsProcessIn(any(), anyList());
        verify(this.binanceApiManager)
                .getOpenOrders(SYMBOL);
        verify(this.binanceApiManager, never())
                .getTickerPrice24(SYMBOL);
        verify(this.botOrderService, never())
                .getBuyOrder(this.tradingSignal);
        verify(this.botOrderService, never())
                .createBuyOrder(any());
        verify(this.binanceApiManager, never())
                .cancelAndNewOrderWithStopLoss(any(), any(), any(), any(), any(), any());

        verify(this.publisher, never())
                .publishEvent(any());
    }

    @Test
    public void testProcessOpenBuyOrders_withNotAvailableToBuy() throws Exception {
        // Arrange
        when(this.tradingSignalService.getTradingSignalsAfterDateAndIsProcessIn(any(), anyList()))
                .thenReturn(List.of(this.tradingSignal));
        when(this.binanceApiManager.getOpenOrders(SYMBOL))
                .thenReturn(List.of(this.orderInfo));
        when(this.binanceApiManager.getTickerPrice24(SYMBOL))
                .thenReturn(this.tickerData);
        when(this.tickerData.getLastPrice())
                .thenReturn(LAST_PRICE_OUT_RANGE);
        when(this.futureTradeService.createFutureTrade(this.tradingSignal, TradeStatus.NOT_IN_RANGE))
                .thenReturn(this.futureTrade);

        //Act
        this.binanceOpenBuyOrderManager.processOpenBuyOrders();

        // Assert
        verify(this.tradingSignalService)
                .getTradingSignalsAfterDateAndIsProcessIn(any(), anyList());
        verify(this.binanceApiManager)
                .getOpenOrders(SYMBOL);
        verify(this.binanceApiManager)
                .getTickerPrice24(SYMBOL);
        verify(this.botOrderService, never())
                .getBuyOrder(this.tradingSignal);
        verify(this.botOrderService, never())
                .createBuyOrder(any());
        verify(this.binanceApiManager, never())
                .cancelAndNewOrderWithStopLoss(any(), any(), any(), any(), any(), any());
        verify(this.publisher, never())
                .publishEvent(any());
    }

    @Test
    public void testProcessOpenBuyOrders_withCancelAndNewOrderWithStopLossApiException() throws Exception {
        // Arrange
        when(this.tradingSignalService.getTradingSignalsAfterDateAndIsProcessIn(any(), anyList()))
                .thenReturn(List.of(this.tradingSignal));
        when(this.binanceApiManager.getOpenOrders(SYMBOL))
                .thenReturn(List.of(this.orderInfo));
        when(this.binanceApiManager.getTickerPrice24(SYMBOL))
                .thenReturn(this.tickerData);
        when(this.botOrderService.getBuyOrder(this.tradingSignal))
                .thenReturn(Optional.of(this.buyOrder));
        when(this.binanceApiManager.cancelAndNewOrderWithStopLoss(any(), any(), any(), any(), any(), any()))
                .thenThrow(RuntimeException.class);
        when(this.futureTradeService.createFutureTrade(this.tradingSignal, TradeStatus.ERROR_BUY))
                .thenReturn(this.futureTrade);

        //Act
        this.binanceOpenBuyOrderManager.processOpenBuyOrders();

        // Assert
        verify(this.tradingSignalService)
                .getTradingSignalsAfterDateAndIsProcessIn(any(), anyList());
        verify(this.binanceApiManager)
                .getOpenOrders(SYMBOL);
        verify(this.binanceApiManager)
                .getTickerPrice24(SYMBOL);
        verify(this.botOrderService)
                .getBuyOrder(this.tradingSignal);
        verify(this.binanceApiManager)
                .cancelAndNewOrderWithStopLoss(any(), any(), any(), any(), any(), any());
        verify(this.futureTradeService)
                .createFutureTrade(this.tradingSignal, TradeStatus.ERROR_BUY);
        verify(this.publisher, never())
                .publishEvent(any());
    }
}
