package com.ozgen.telegrambinancebot.manager.binance;


import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.model.TradingStrategy;
import com.ozgen.telegrambinancebot.model.binance.AssetBalance;
import com.ozgen.telegrambinancebot.model.binance.OrderResponse;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.bot.FutureTrade;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.NewBuyOrderEvent;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BinanceBuyOrderManagerTest {

    private static final String SYMBOL = "BNBBTC";
    private static final String LAST_PRICE = "0.01";
    private static final String LAST_PRICE_OUT_RANGE = "0.02";
    private static final String START_ENTRY = "0.009";
    private static final String END_ENTRY = "0.011";
    private static final String CURRENCY_RATE = "BTCUSD";
    private static final Double AMOUNT = 100.0;
    private static final Double PERCENTAGE_INC = 0.5;
    private static final Double PROFIT_PERCENTAGE = 6.0;
    private static final float BINANCE_FEE_PERCENTAGE = 0.00201f;
    private static final String CURRENCY = "BTC";
    private static final Double BTC_AMOUNT = 0.1;
    private static final String BTCUSD_PRICE = "42000";
    private static final String STOPLOSS = "0.008";

    @Mock
    private BinanceApiManager binanceApiManager;
    @Mock
    private ApplicationEventPublisher publisher;
    @Mock
    private BotConfiguration botConfiguration;
    @Mock
    private FutureTradeService futureTradeService;
    @Mock
    private BotOrderService botOrderService;
    @Mock
    private BinanceHelper binanceHelper;

    @InjectMocks
    private BinanceBuyOrderManager binanceBuyOrderManager;

    @Mock
    private TickerData tickerData;
    @Mock
    private TickerData tickerDataBTCUSD;
    @Mock
    private TradingSignal tradingSignal;
    @Mock
    private AssetBalance assetBalance;
    @Mock
    private OrderResponse orderResponse;
    @Mock
    private FutureTrade futureTrade;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(this.tradingSignal.getSymbol())
                .thenReturn(SYMBOL);
        when(this.tradingSignal.getEntryStart())
                .thenReturn(START_ENTRY);
        when(this.tradingSignal.getEntryEnd())
                .thenReturn(END_ENTRY);
        when(this.tradingSignal.getStopLoss())
                .thenReturn(STOPLOSS);
        when(this.tradingSignal.getInvestAmount())
                .thenReturn(null);
        when(this.tradingSignal.getStrategy())
                .thenReturn(TradingStrategy.DEFAULT);

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
        when(this.botConfiguration.getBinanceFeePercentage())
                .thenReturn(BINANCE_FEE_PERCENTAGE);

        when(this.binanceApiManager.getTickerPrice24(CURRENCY_RATE))
                .thenReturn(this.tickerDataBTCUSD);
        when(this.tickerDataBTCUSD.getLastPrice())
                .thenReturn(BTCUSD_PRICE);
    }

    @Test
    void testProcessNewBuyOrderEvent_Success() throws Exception {
        // Arrange
        double expectedBuyPrice = (Double.parseDouble(LAST_PRICE) * (PERCENTAGE_INC / 100) + Double.parseDouble(LAST_PRICE));
        double binanceFee = AMOUNT * BINANCE_FEE_PERCENTAGE;
        double expectedCoinAmount = ((AMOUNT - binanceFee) / Double.parseDouble(BTCUSD_PRICE)) / expectedBuyPrice;
        when(this.tickerData.getLastPrice())
                .thenReturn(LAST_PRICE);
        when(this.binanceApiManager.getUserAsset())
                .thenReturn(List.of(this.assetBalance));
        when(this.assetBalance.getAsset())
                .thenReturn(CURRENCY);
        when(this.assetBalance.getFree())
                .thenReturn(String.valueOf(BTC_AMOUNT));
        when(this.botOrderService.getBuyOrder(this.tradingSignal))
                .thenReturn(Optional.empty());
        when(this.binanceApiManager.newOrderWithStopLoss(any(), any(), any(), any()))
                .thenReturn(this.orderResponse);
        when(this.binanceHelper.hasAccountEnoughAsset(anyList(), any()))
                .thenReturn(true);
        when(this.binanceHelper.calculateCoinAmount(expectedBuyPrice, this.tradingSignal))
                .thenReturn(expectedCoinAmount);
        when(this.botOrderService.createBuyOrder(any(BuyOrder.class)))
                .thenAnswer((Answer<BuyOrder>) invocation -> (BuyOrder) invocation.getArguments()[0]);
        NewBuyOrderEvent event = new NewBuyOrderEvent(this, this.tradingSignal, this.tickerData);

        // Act
        this.binanceBuyOrderManager.processNewBuyOrderEvent(event);

        // Assert
        verify(this.binanceApiManager)
                .getUserAsset();
        verify(this.botOrderService)
                .getBuyOrder(this.tradingSignal);
        ArgumentCaptor<BuyOrder> buyOrderCaptor = ArgumentCaptor.forClass(BuyOrder.class);
        verify(this.botOrderService)
                .createBuyOrder(buyOrderCaptor.capture());
        BuyOrder buyOrder = buyOrderCaptor.getValue();
        double expectedStopLoss = Double.parseDouble(END_ENTRY);
        assertEquals(expectedCoinAmount, buyOrder.getCoinAmount());
        assertEquals(expectedCoinAmount, buyOrder.getTotalCoinAmount());
        assertEquals(expectedBuyPrice, buyOrder.getBuyPrice());
        assertEquals(expectedBuyPrice, buyOrder.getBuyPrice());
        assertEquals(expectedStopLoss, buyOrder.getStopLoss());
        assertEquals(this.tradingSignal, buyOrder.getTradingSignal());
        assertEquals(SYMBOL, buyOrder.getSymbol());
        verify(this.binanceApiManager)
                .newOrder(SYMBOL, expectedBuyPrice, expectedCoinAmount);
        ArgumentCaptor<NewSellOrderEvent> eventCaptor = ArgumentCaptor.forClass(NewSellOrderEvent.class);
        verify(this.publisher, times(1))
                .publishEvent(eventCaptor.capture());
        NewSellOrderEvent newSellOrderEvent = eventCaptor.getValue();
        BuyOrder eventBuyOrder = newSellOrderEvent.getBuyOrder();
        assertEquals(buyOrder, eventBuyOrder);
    }

    @Test
    void testProcessNewBuyOrderEvent_withHasNotEnoughBtc() throws Exception {
        // Arrange
        when(this.binanceHelper.hasAccountEnoughAsset(anyList(), any()))
                .thenReturn(false);

        when(this.futureTradeService.createFutureTrade(this.tradingSignal, TradeStatus.INSUFFICIENT))
                .thenReturn(this.futureTrade);
        NewBuyOrderEvent event = new NewBuyOrderEvent(this, this.tradingSignal, this.tickerData);

        // Act
        this.binanceBuyOrderManager.processNewBuyOrderEvent(event);

        // Assert
        verify(this.binanceApiManager)
                .getUserAsset();
        verify(this.futureTradeService)
                .createFutureTrade(this.tradingSignal, TradeStatus.INSUFFICIENT);
        verify(this.botOrderService, never())
                .getBuyOrder(this.tradingSignal);
        verify(this.botOrderService, never())
                .createBuyOrder(any());
        verify(this.binanceApiManager, never())
                .newOrder(any(), any(), any());
        verify(this.publisher, never())
                .publishEvent(any());
    }

    @Test
    void testProcessNewBuyOrderEvent_withSnapShotApiException() throws Exception {
        // Arrange
        when(this.binanceApiManager.getUserAsset())
                .thenThrow(RuntimeException.class);
        when(this.futureTradeService.createFutureTrade(this.tradingSignal, TradeStatus.ERROR_BUY))
                .thenReturn(this.futureTrade);
        NewBuyOrderEvent event = new NewBuyOrderEvent(this, this.tradingSignal, this.tickerData);

        // Act
        this.binanceBuyOrderManager.processNewBuyOrderEvent(event);

        // Assert
        verify(this.binanceApiManager)
                .getUserAsset();
        verify(this.futureTradeService)
                .createFutureTrade(this.tradingSignal, TradeStatus.ERROR_BUY);
        verify(this.botOrderService, never())
                .getBuyOrder(this.tradingSignal);
        verify(this.botOrderService, never())
                .createBuyOrder(any());
        verify(this.binanceApiManager, never())
                .newOrderWithStopLoss(any(), any(), any(), any());
        verify(this.publisher, never())
                .publishEvent(any(NewSellOrderEvent.class));
        verify(this.publisher)
                .publishEvent(any(ErrorEvent.class));
    }

    @Test
    void testProcessNewBuyOrderEvent_withPriceNotInRange() throws Exception {
        // Arrange
        when(this.tickerData.getLastPrice())
                .thenReturn(LAST_PRICE_OUT_RANGE);
        when(this.binanceApiManager.getUserAsset())
                .thenReturn(List.of(this.assetBalance));
        when(this.assetBalance.getAsset())
                .thenReturn(CURRENCY);
        when(this.assetBalance.getFree())
                .thenReturn(String.valueOf(BTC_AMOUNT));
        when(this.binanceHelper.hasAccountEnoughAsset(anyList(), any()))
                .thenReturn(true);
        when(this.futureTradeService.createFutureTrade(this.tradingSignal, TradeStatus.NOT_IN_RANGE))
                .thenReturn(this.futureTrade);
        NewBuyOrderEvent event = new NewBuyOrderEvent(this, this.tradingSignal, this.tickerData);

        // Act
        this.binanceBuyOrderManager.processNewBuyOrderEvent(event);

        // Assert
        verify(this.binanceApiManager)
                .getUserAsset();
        verify(this.futureTradeService)
                .createFutureTrade(this.tradingSignal, TradeStatus.NOT_IN_RANGE);
        verify(this.botOrderService, never())
                .getBuyOrder(this.tradingSignal);
        verify(this.botOrderService, never())
                .createBuyOrder(any());
        verify(this.binanceApiManager, never())
                .newOrderWithStopLoss(any(), any(), any(), any());
        verify(this.publisher, never())
                .publishEvent(any());
    }

    @Test
    void testProcessNewBuyOrderEvent_withNewOrderWithStopLossApiException() throws Exception {
        // Arrange
        when(this.tickerData.getLastPrice())
                .thenReturn(LAST_PRICE);
        when(this.binanceApiManager.getUserAsset())
                .thenReturn(List.of(this.assetBalance));
        when(this.assetBalance.getAsset())
                .thenReturn(CURRENCY);
        when(this.assetBalance.getFree())
                .thenReturn(String.valueOf(BTC_AMOUNT));
        when(this.botOrderService.getBuyOrder(this.tradingSignal))
                .thenReturn(Optional.empty());
        when(this.binanceApiManager.newOrder(any(), any(), any()))
                .thenThrow(RuntimeException.class);
        double expectedBuyPrice = (Double.parseDouble(LAST_PRICE) * (PERCENTAGE_INC / 100) + Double.parseDouble(LAST_PRICE));
        double binanceFee = AMOUNT * BINANCE_FEE_PERCENTAGE;
        double expectedCoinAmount = ((AMOUNT - binanceFee) / Double.parseDouble(BTCUSD_PRICE)) / expectedBuyPrice;
        when(this.binanceHelper.hasAccountEnoughAsset(anyList(), any()))
                .thenReturn(true);
        when(this.binanceHelper.calculateCoinAmount(expectedBuyPrice, this.tradingSignal))
                .thenReturn(expectedCoinAmount);
        when(this.futureTradeService.createFutureTrade(this.tradingSignal, TradeStatus.ERROR_BUY))
                .thenReturn(this.futureTrade);
        NewBuyOrderEvent event = new NewBuyOrderEvent(this, this.tradingSignal, this.tickerData);

        // Act
        this.binanceBuyOrderManager.processNewBuyOrderEvent(event);

        // Assert
        verify(this.binanceApiManager)
                .getUserAsset();
        verify(this.botOrderService)
                .getBuyOrder(this.tradingSignal);
        verify(this.binanceApiManager)
                .newOrder(any(), any(), any());
        verify(this.futureTradeService)
                .createFutureTrade(this.tradingSignal, TradeStatus.ERROR_BUY);
        verify(this.botOrderService, never())
                .createBuyOrder(any());
        verify(this.publisher, never())
                .publishEvent(any(NewSellOrderEvent.class));
        verify(this.publisher)
                .publishEvent(any(ErrorEvent.class));
    }
}
