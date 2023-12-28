package com.ozgen.telegrambinancebot.manager.binance;


import com.ozgen.telegrambinancebot.configuration.properties.ScheduleConfiguration;
import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import com.ozgen.telegrambinancebot.utils.SyncUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BinanceOpenSellOrderManagerTest {

    private static final String SYMBOL = "BNBBTC";
    private static final Double BUY_PRICE = 0.01;
    private static final String START_ENTRY = "0.009";
    private static final String END_ENTRY = "0.011";

    private static final String STOPLOSS = "0.008";
    private static final Double BNB_TOTAL_AMOUNT = 0.23;

    @Mock
    private TradingSignalService tradingSignalService;
    @Mock
    private BotOrderService botOrderService;
    @Mock
    private ApplicationEventPublisher publisher;
    @Mock
    private ScheduleConfiguration scheduleConfiguration;

    @InjectMocks
    private BinanceOpenSellOrderManager binanceOpenSellOrderManager;

    @Mock
    private TradingSignal tradingSignal;

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
        when(this.tradingSignal.getIsProcessed())
                .thenReturn(ProcessStatus.SELL);

        when(this.scheduleConfiguration.getMonthBefore())
                .thenReturn(1);

        this.buyOrder = new BuyOrder();
        this.buyOrder.setSymbol(SYMBOL);
        this.buyOrder.setBuyPrice(BUY_PRICE);
        this.buyOrder.setCoinAmount(BNB_TOTAL_AMOUNT);
        this.buyOrder.setStopLoss(0.012);
        this.buyOrder.setStopLossLimit(0.013);
        this.buyOrder.setTradingSignal(this.tradingSignal);
    }

    @Test
    public void testProcessOpenSellOrders_Success() {
        // Arrange
        when(this.tradingSignalService.getTradingSignalsAfterDateAndIsProcessIn(any(), argThat(list -> list.contains(ProcessStatus.SELL))))
                .thenReturn(List.of(this.tradingSignal));
        when(this.botOrderService.getBuyOrders(argThat(list -> list.contains(this.tradingSignal))))
                .thenReturn(List.of(this.buyOrder));

        // Act
        this.binanceOpenSellOrderManager.processOpenSellOrders();

        // Assert
        verify(this.tradingSignalService)
                .getTradingSignalsAfterDateAndIsProcessIn(any(), argThat(list -> list.contains(ProcessStatus.SELL)));
        verify(this.botOrderService)
                .getBuyOrders(argThat(list -> list.contains(this.tradingSignal)));
        ArgumentCaptor<NewSellOrderEvent> eventCaptor = ArgumentCaptor.forClass(NewSellOrderEvent.class);
        verify(this.publisher)
                .publishEvent(eventCaptor.capture());
        NewSellOrderEvent newSellOrderEvent = eventCaptor.getValue();
        assertEquals(this.buyOrder, newSellOrderEvent.getBuyOrder());
    }

    @Test
    public void testProcessOpenSellOrders_withEmptyBuyOrders() {
        // Arrange
        when(this.tradingSignalService.getTradingSignalsAfterDateAndIsProcessIn(any(), argThat(list -> list.contains(ProcessStatus.SELL))))
                .thenReturn(List.of(this.tradingSignal));
        when(this.botOrderService.getBuyOrders(argThat(list -> list.contains(this.tradingSignal))))
                .thenReturn(List.of());

        // Act
        this.binanceOpenSellOrderManager.processOpenSellOrders();

        // Assert
        verify(this.tradingSignalService)
                .getTradingSignalsAfterDateAndIsProcessIn(any(), argThat(list -> list.contains(ProcessStatus.SELL)));
        verify(this.botOrderService)
                .getBuyOrders(argThat(list -> list.contains(this.tradingSignal)));
        verify(this.publisher, never())
                .publishEvent(any());
    }

    @Test
    public void testProcessOpenSellOrders_withEmptyTradingSignalList() {
        // Arrange
        when(this.tradingSignalService.getTradingSignalsAfterDateAndIsProcessIn(any(), argThat(list -> list.contains(ProcessStatus.SELL))))
                .thenReturn(List.of());

        // Act
        this.binanceOpenSellOrderManager.processOpenSellOrders();

        // Assert
        verify(this.tradingSignalService)
                .getTradingSignalsAfterDateAndIsProcessIn(any(), argThat(list -> list.contains(ProcessStatus.SELL)));
        verify(this.botOrderService, never())
                .getBuyOrders(any());
        verify(this.publisher, never())
                .publishEvent(any());
    }

    @Test
    @Disabled
    public void testProcessOpenSellOrders_withException() {
        // Arrange
        MockedStatic<SyncUtil> mockedSyncUtil = mockStatic(SyncUtil.class);
        try {
            when(this.tradingSignalService.getTradingSignalsAfterDateAndIsProcessIn(any(), argThat(list -> list.contains(ProcessStatus.SELL))))
                    .thenReturn(List.of(this.tradingSignal));
            when(this.botOrderService.getBuyOrders(argThat(list -> list.contains(this.tradingSignal))))
                    .thenReturn(List.of(this.buyOrder));
            RuntimeException testException = new RuntimeException("Test Exception");
            mockedSyncUtil.when(SyncUtil::pauseBetweenOperations).thenThrow(testException);

            // Act
            this.binanceOpenSellOrderManager.processOpenSellOrders();

            // Assert
            verify(this.tradingSignalService)
                    .getTradingSignalsAfterDateAndIsProcessIn(any(), argThat(list -> list.contains(ProcessStatus.SELL)));
            verify(this.botOrderService)
                    .getBuyOrders(argThat(list -> list.contains(this.tradingSignal)));
            ArgumentCaptor<NewSellOrderEvent> eventCaptor = ArgumentCaptor.forClass(NewSellOrderEvent.class);
            verify(this.publisher)
                    .publishEvent(eventCaptor.capture());
            NewSellOrderEvent newSellOrderEvent = eventCaptor.getValue();
            assertEquals(this.buyOrder, newSellOrderEvent.getBuyOrder());
        } finally {
            mockedSyncUtil.close();
        }
    }
}
