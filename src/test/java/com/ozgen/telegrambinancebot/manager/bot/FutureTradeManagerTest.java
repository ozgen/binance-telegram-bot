package com.ozgen.telegrambinancebot.manager.bot;


import com.ozgen.telegrambinancebot.manager.binance.BinanceApiManager;
import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.bot.FutureTrade;
import com.ozgen.telegrambinancebot.model.events.NewBuyOrderEvent;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.FutureTradeService;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import com.ozgen.telegrambinancebot.utils.SyncUtil;
import com.ozgen.telegrambinancebot.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FutureTradeManagerTest {
    @Mock
    private BinanceApiManager binanceApiManager;
    @Mock
    private FutureTradeService futureTradeService;
    @Mock
    private TradingSignalService tradingSignalService;
    @Mock
    private BotOrderService botOrderService;
    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private TickerData tickerData;
    @Mock
    private BuyOrder buyOrder;

    @InjectMocks
    private FutureTradeManager futureTradeManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessFutureTrades_withInsufficientStatus() throws Exception {
        // Arrange
        TradeStatus tradeStatus = TradeStatus.INSUFFICIENT;
        String tradingSignalId = UUID.randomUUID().toString();
        FutureTrade futureTrade = new FutureTrade();
        futureTrade.setTradeStatus(tradeStatus);
        futureTrade.setTradeSignalId(tradingSignalId);
        List<FutureTrade> futureTrades = List.of(futureTrade);
        TradingSignal tradingSignal = TestData.getTradingSignal();
        tradingSignal.setId(tradingSignalId);
        List<TradingSignal> tradingSignals = List.of(tradingSignal);
        when(this.futureTradeService.getAllFutureTradeByTradeStatus(tradeStatus))
                .thenReturn(futureTrades);
        when(this.futureTradeService.getAllFutureTradeByTradingSignals(argThat(list -> list.contains(tradingSignalId))))
                .thenReturn(futureTrades);
        when(this.tradingSignalService.getTradingSignalsByIdList(argThat(list -> list.contains(tradingSignalId))))
                .thenReturn(tradingSignals);
        when(this.binanceApiManager.getTickerPrice24(tradingSignal.getSymbol()))
                .thenReturn(tickerData);
        MockedStatic<SyncUtil> mockedSyncUtil = mockStatic(SyncUtil.class);
        mockedSyncUtil.when(SyncUtil::pauseBetweenOperations)
                .thenAnswer(invocation -> null);

        // Act
        this.futureTradeManager.processFutureTrades(TradeStatus.INSUFFICIENT);

        // Assert
        verify(this.futureTradeService)
                .getAllFutureTradeByTradeStatus(tradeStatus);
        verify(this.tradingSignalService)
                .getTradingSignalsByIdList(anyList());
        verify(this.futureTradeService)
                .getAllFutureTradeByTradingSignals(anyList());
        verify(this.binanceApiManager)
                .getTickerPrice24(tradingSignal.getSymbol());
        ArgumentCaptor<NewBuyOrderEvent> eventCaptor = ArgumentCaptor.forClass(NewBuyOrderEvent.class);
        verify(this.publisher, times(1))
                .publishEvent(eventCaptor.capture());
        NewBuyOrderEvent newBuyOrderEvent = eventCaptor.getValue();
        assertEquals(this.tickerData, newBuyOrderEvent.getTickerData());
        assertEquals(tradingSignal, newBuyOrderEvent.getTradingSignal());
        verify(futureTradeService)
                .deleteFutureTrades(futureTrades);
        mockedSyncUtil.verify(SyncUtil::pauseBetweenOperations, VerificationModeFactory.times(2));
    }

    @Test
    public void testProcessFutureTrades_withInvalidStatus() throws Exception {
        // Arrange
        TradeStatus tradeStatus = TradeStatus.ERROR_SELL;

        // Act
        this.futureTradeManager.processFutureTrades(tradeStatus);

        // Assert
        verify(this.futureTradeService, never())
                .getAllFutureTradeByTradeStatus(tradeStatus);
        verify(this.tradingSignalService, never())
                .getTradingSignalsByIdList(anyList());
        verify(this.futureTradeService, never())
                .getAllFutureTradeByTradingSignals(anyList());
        verify(this.binanceApiManager, never())
                .getTickerPrice24(any());
        verify(this.publisher, never())
                .publishEvent(any());
        verify(futureTradeService, never())
                .deleteFutureTrades(anyList());
    }

    @Test
    public void testProcessFutureTrades_withInsufficientStatusAndDuplicateFutureTrades() throws Exception {
        // Arrange
        TradeStatus tradeStatus = TradeStatus.INSUFFICIENT;
        String tradingSignalId = UUID.randomUUID().toString();
        FutureTrade futureTrade = new FutureTrade();
        futureTrade.setTradeStatus(tradeStatus);
        futureTrade.setTradeSignalId(tradingSignalId);
        List<FutureTrade> futureTrades = List.of(futureTrade);
        FutureTrade duplicateFutureTrade = new FutureTrade();
        duplicateFutureTrade.setTradeStatus(tradeStatus);
        duplicateFutureTrade.setTradeSignalId(tradingSignalId);
        List<FutureTrade> futureTradesWithDuplicate = List.of(futureTrade, duplicateFutureTrade);
        TradingSignal tradingSignal = TestData.getTradingSignal();
        tradingSignal.setId(tradingSignalId);
        List<TradingSignal> tradingSignals = List.of(tradingSignal);
        when(this.futureTradeService.getAllFutureTradeByTradeStatus(tradeStatus))
                .thenReturn(futureTrades);
        when(this.futureTradeService.getAllFutureTradeByTradingSignals(argThat(list -> list.contains(tradingSignalId))))
                .thenReturn(futureTradesWithDuplicate);
        when(this.tradingSignalService.getTradingSignalsByIdList(argThat(list -> list.contains(tradingSignalId))))
                .thenReturn(tradingSignals);

        // Act
        this.futureTradeManager.processFutureTrades(TradeStatus.INSUFFICIENT);

        // Assert
        verify(this.futureTradeService)
                .getAllFutureTradeByTradeStatus(tradeStatus);
        verify(this.tradingSignalService)
                .getTradingSignalsByIdList(anyList());
        verify(this.futureTradeService)
                .getAllFutureTradeByTradingSignals(anyList());
        verify(this.binanceApiManager, never())
                .getTickerPrice24(any());
        verify(this.publisher, never())
                .publishEvent(any());
        verify(futureTradeService)
                .deleteFutureTrades(futureTradesWithDuplicate);
    }

    @Test
    public void testProcessFutureTrades_withInsufficientStatusWithNullTickerData() throws Exception {
        // Arrange
        TradeStatus tradeStatus = TradeStatus.INSUFFICIENT;
        String tradingSignalId = UUID.randomUUID().toString();
        FutureTrade futureTrade = new FutureTrade();
        futureTrade.setTradeStatus(tradeStatus);
        futureTrade.setTradeSignalId(tradingSignalId);
        List<FutureTrade> futureTrades = List.of(futureTrade);
        TradingSignal tradingSignal = TestData.getTradingSignal();
        tradingSignal.setId(tradingSignalId);
        List<TradingSignal> tradingSignals = List.of(tradingSignal);
        when(this.futureTradeService.getAllFutureTradeByTradeStatus(tradeStatus))
                .thenReturn(futureTrades);
        when(this.futureTradeService.getAllFutureTradeByTradingSignals(argThat(list -> list.contains(tradingSignalId))))
                .thenReturn(futureTrades);
        when(this.tradingSignalService.getTradingSignalsByIdList(argThat(list -> list.contains(tradingSignalId))))
                .thenReturn(tradingSignals);
        when(this.binanceApiManager.getTickerPrice24(tradingSignal.getSymbol()))
                .thenThrow(RuntimeException.class);
        // Act
        this.futureTradeManager.processFutureTrades(TradeStatus.INSUFFICIENT);

        // Assert
        verify(this.futureTradeService)
                .getAllFutureTradeByTradeStatus(tradeStatus);
        verify(this.tradingSignalService)
                .getTradingSignalsByIdList(anyList());
        verify(this.futureTradeService)
                .getAllFutureTradeByTradingSignals(anyList());
        verify(this.binanceApiManager)
                .getTickerPrice24(tradingSignal.getSymbol());
        verify(this.publisher, never())
                .publishEvent(any());
        verify(futureTradeService, never())
                .deleteFutureTrades(anyList());
    }

    @Test
    public void testProcessSellErrorFutureTrades_withSellErrorFutureTrades(){
        // Arrange
        TradeStatus tradeStatus = TradeStatus.ERROR_SELL;
        String tradingSignalId = UUID.randomUUID().toString();
        FutureTrade futureTrade = new FutureTrade();
        futureTrade.setTradeStatus(tradeStatus);
        futureTrade.setTradeSignalId(tradingSignalId);
        List<FutureTrade> futureTrades = List.of(futureTrade);
        TradingSignal tradingSignal = TestData.getTradingSignal();
        tradingSignal.setId(tradingSignalId);
        List<TradingSignal> tradingSignals = List.of(tradingSignal);
        when(this.futureTradeService.getAllFutureTradeByTradeStatus(tradeStatus))
                .thenReturn(futureTrades);
        when(this.tradingSignalService.getTradingSignalsByIdList(argThat(list -> list.contains(tradingSignalId))))
                .thenReturn(tradingSignals);
        when(this.botOrderService.getBuyOrders(tradingSignals))
                .thenReturn(List.of(this.buyOrder));

        // Act
        this.futureTradeManager.processSellErrorFutureTrades();

        // Assert
        verify(this.futureTradeService)
                .getAllFutureTradeByTradeStatus(tradeStatus);
        verify(this.tradingSignalService)
                .getTradingSignalsByIdList(anyList());
        verify(this.botOrderService)
                .getBuyOrders(tradingSignals);
        ArgumentCaptor<NewSellOrderEvent> eventCaptor = ArgumentCaptor.forClass(NewSellOrderEvent.class);
        verify(publisher)
                .publishEvent(eventCaptor.capture());
        NewSellOrderEvent newSellOrderEvent = eventCaptor.getValue();
        assertEquals(this.buyOrder, newSellOrderEvent.getBuyOrder());
        verify(this.futureTradeService)
                .deleteFutureTrades(futureTrades);
    }
 @Test
    public void testProcessSellErrorFutureTrades_withSellErrorFutureTradesAndEmptyBuyOrderList(){
        // Arrange
        TradeStatus tradeStatus = TradeStatus.ERROR_SELL;
        String tradingSignalId = UUID.randomUUID().toString();
        FutureTrade futureTrade = new FutureTrade();
        futureTrade.setTradeStatus(tradeStatus);
        futureTrade.setTradeSignalId(tradingSignalId);
        List<FutureTrade> futureTrades = List.of(futureTrade);
        TradingSignal tradingSignal = TestData.getTradingSignal();
        tradingSignal.setId(tradingSignalId);
        List<TradingSignal> tradingSignals = List.of(tradingSignal);
        when(this.futureTradeService.getAllFutureTradeByTradeStatus(tradeStatus))
                .thenReturn(futureTrades);
        when(this.tradingSignalService.getTradingSignalsByIdList(argThat(list -> list.contains(tradingSignalId))))
                .thenReturn(tradingSignals);
        when(this.botOrderService.getBuyOrders(tradingSignals))
                .thenReturn(List.of());

        // Act
        this.futureTradeManager.processSellErrorFutureTrades();

        // Assert
        verify(this.futureTradeService)
                .getAllFutureTradeByTradeStatus(tradeStatus);
        verify(this.tradingSignalService)
                .getTradingSignalsByIdList(anyList());
        verify(this.botOrderService)
                .getBuyOrders(tradingSignals);
        verify(publisher, never())
                .publishEvent(any());

        verify(this.futureTradeService)
                .deleteFutureTrades(futureTrades);
    }
}
