package com.ozgen.telegrambinancebot.manager.binance;


import com.ozgen.telegrambinancebot.adapters.binance.BinanceAPI;
import com.ozgen.telegrambinancebot.model.binance.CancelAndNewOrderResponse;
import com.ozgen.telegrambinancebot.model.binance.OpenOrder;
import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
import com.ozgen.telegrambinancebot.model.binance.OrderResponse;
import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.service.AccountSnapshotService;
import com.ozgen.telegrambinancebot.service.BinanceOrderService;
import com.ozgen.telegrambinancebot.service.TickerDataService;
import com.ozgen.telegrambinancebot.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BinanceApiManagerTest {

    @Mock
    private BinanceAPI binanceAPI;

    @Mock
    private TickerDataService tickerDataService;

    @Mock
    private  BinanceOrderService binanceOrderService;

    @Mock
    private  AccountSnapshotService accountSnapshotService;

    @InjectMocks
    private BinanceApiManager binanceApiManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTickerPrice24_Success() throws Exception {
        // Arrange
        String symbol = "BNBBTC";
        String tickerPrice24Json = TestData.TICKER_DATA;
        when(this.binanceAPI.getTickerPrice24(symbol))
                .thenReturn(tickerPrice24Json);
        when(this.tickerDataService.createTickerData(any(TickerData.class)))
                .thenAnswer((Answer<TickerData>) invocation -> (TickerData) invocation.getArguments()[0]);

        // Act
        TickerData result = this.binanceApiManager.getTickerPrice24(symbol);

        // Assert
        verify(this.binanceAPI)
                .getTickerPrice24(symbol);
        verify(this.tickerDataService)
                .createTickerData(any(TickerData.class));
        assertNotNull(result);
    }

    @Test
    void testGetTickerPrice24_ApiException() {
        // Arrange
        String symbol = "BNBBTC";
        when(this.binanceAPI.getTickerPrice24(symbol))
                .thenThrow(new RuntimeException("API call failed"));

        // Act & Assert
        assertThrows(Exception.class, () -> this.binanceApiManager.getTickerPrice24(symbol));
        verify(this.binanceAPI)
                .getTickerPrice24(symbol);
        verify(this.tickerDataService, never())
                .createTickerData(any(TickerData.class));
    }

    @Test
    void testGetAccountSnapshot_Success() throws Exception {
        // Arrange
        String snapshotDataJson = TestData.SNAPSHOT_DATA;
        when(this.binanceAPI.getAccountSnapshot())
                .thenReturn(snapshotDataJson);
        when(this.accountSnapshotService.createSnapshotData(any(SnapshotData.class)))
                .thenAnswer((Answer<SnapshotData>) invocation -> (SnapshotData) invocation.getArguments()[0]);

        // Act
        SnapshotData result = this.binanceApiManager.getAccountSnapshot();

        // Assert
        verify(this.binanceAPI)
                .getAccountSnapshot();
//        verify(this.accountSnapshotService)
//                .createSnapshotData(any(SnapshotData.class));
        assertNotNull(result);
    }

    @Test
    void testGetAccountSnapshot_ApiException() throws Exception {
        // Arrange
        String snapshotDataJson = TestData.SNAPSHOT_DATA;
        when(this.binanceAPI.getAccountSnapshot())
                .thenThrow(new RuntimeException("API call failed"));

        // Act & Assert
        assertThrows(Exception.class, () -> this.binanceApiManager.getAccountSnapshot());
        verify(this.binanceAPI)
                .getAccountSnapshot();
        verify(this.accountSnapshotService, never())
                .createSnapshotData(any(SnapshotData.class));
    }

    @Test
    void testGetOpenOrders_Success() throws Exception {
        // Arrange
        String symbol = "BNBBTC";
        String openOrderJson = TestData.ORDER_INFO_ARRAY;
        when(this.binanceAPI.getOpenOrders(symbol))
                .thenReturn(openOrderJson);
        when(this.binanceOrderService.createOrderInfos(anyList()))
                .thenAnswer((Answer<List>) invocation -> (List) invocation.getArguments()[0]);

        // Act
        List<OrderInfo> result = this.binanceApiManager.getOpenOrders(symbol);

        // Assert
        verify(this.binanceAPI)
                .getOpenOrders(symbol);
        verify(this.binanceOrderService)
                .createOrderInfos(anyList());
        assertNotNull(result);
    }

    @Test
    void testGetOpenOrders_ApiException() {
        // Arrange
        String symbol = "BNBBTC";
        when(this.binanceAPI.getOpenOrders(symbol))
                .thenThrow(new RuntimeException("API call failed"));

        // Act & Assert
        assertThrows(Exception.class, () -> this.binanceApiManager.getOpenOrders(symbol));
        verify(this.binanceAPI)
                .getOpenOrders(symbol);
        verify(this.binanceOrderService, never())
                .createOrderInfos(anyList());
    }

    @Test
    void testCancelOpenOrders_Success() throws Exception {
        // Arrange
        String symbol = "BNBBTC";
        String openOrderJson = TestData.OPEN_ORDER_ARRAY;
        when(this.binanceAPI.cancelOpenOrders(symbol))
                .thenReturn(openOrderJson);
        when(this.binanceOrderService.createOpenOrders(anyList()))
                .thenAnswer((Answer<List>) invocation -> (List) invocation.getArguments()[0]);

        // Act
        List<OpenOrder> result = this.binanceApiManager.cancelOpenOrders(symbol);

        // Assert
        verify(this.binanceAPI)
                .cancelOpenOrders(symbol);
        verify(this.binanceOrderService)
                .createOpenOrders(anyList());
        assertNotNull(result);
    }

    @Test
    void testCancelOpenOrders_ApiException() {
        // Arrange
        String symbol = "BNBBTC";
        when(this.binanceAPI.cancelOpenOrders(symbol))
                .thenThrow(new RuntimeException("API call failed"));

        // Act & Assert
        assertThrows(Exception.class, () -> this.binanceApiManager.cancelOpenOrders(symbol));
        verify(this.binanceAPI)
                .cancelOpenOrders(symbol);
        verify(this.binanceOrderService, never())
                .createOpenOrders(anyList());
    }

    @Test
    void testNewOrderWithStopLoss_Success() throws Exception {
        // Arrange
        String symbol = "BTCUSDT";
        Double price = 10000.0;
        Double quantity = 1.0;
        Double stopPrice = 9500.0;
        String orderResponseJson = TestData.ORDER_RESPONSE;
        when(this.binanceAPI.newOrderWithStopLoss(symbol, price, quantity, stopPrice))
                .thenReturn(orderResponseJson);
        when(this.binanceOrderService.createOrderResponse(any(OrderResponse.class)))
                .thenAnswer((Answer<OrderResponse>) invocation -> (OrderResponse) invocation.getArguments()[0]);

        // Act
        OrderResponse result = this.binanceApiManager.newOrderWithStopLoss(symbol, price, quantity, stopPrice);

        // Assert
        verify(this.binanceAPI)
                .newOrderWithStopLoss(symbol, price, quantity, stopPrice);
        verify(this.binanceOrderService)
                .createOrderResponse(any(OrderResponse.class));
        assertNotNull(result);
    }

    @Test
    void testNewOrderWithStopLoss_ApiException() {
        // Arrange
        String symbol = "BTCUSDT";
        Double price = 10000.0;
        Double quantity = 1.0;
        Double stopPrice = 9500.0;
        when(this.binanceAPI.newOrderWithStopLoss(symbol, price, quantity, stopPrice))
                .thenThrow(new RuntimeException("API call failed"));

        // Act & Assert
        assertThrows(Exception.class, () -> this.binanceApiManager.newOrderWithStopLoss(symbol, price, quantity, stopPrice));
        verify(this.binanceAPI)
                .newOrderWithStopLoss(symbol, price, quantity, stopPrice);
        verify(this.binanceOrderService, never())
                .createOrderResponse(any(OrderResponse.class));
    }

    @Test
    void testCancelAndNewOrderWithStopLoss_Success() throws Exception {
        // Arrange
        String symbol = "BTCUSDT";
        Double price = 10000.0;
        Double quantity = 1.0;
        Double stopPrice = 9500.0;
        Long cancelOrderId = 1l;
        String orderResponseJson = TestData.CANCEL_AND_NEW_ORDER_RESPONSE;
        when(this.binanceAPI.cancelAndNewOrderWithStopLoss(symbol, price, quantity, stopPrice, cancelOrderId))
                .thenReturn(orderResponseJson);
        when(this.binanceOrderService.createCancelAndNewOrderResponse(any(CancelAndNewOrderResponse.class)))
                .thenAnswer((Answer<CancelAndNewOrderResponse>) invocation -> (CancelAndNewOrderResponse) invocation.getArguments()[0]);

        // Act
        CancelAndNewOrderResponse result = this.binanceApiManager.cancelAndNewOrderWithStopLoss(symbol, price, quantity, stopPrice, cancelOrderId);

        // Assert
        verify(this.binanceAPI)
                .cancelAndNewOrderWithStopLoss(symbol, price, quantity, stopPrice, cancelOrderId);
        verify(this.binanceOrderService)
                .createCancelAndNewOrderResponse(any(CancelAndNewOrderResponse.class));
        assertNotNull(result);
    }

    @Test
    void testCancelAndNewOrderWithStopLoss_ApiException() {
        // Arrange
        String symbol = "BTCUSDT";
        Double price = 10000.0;
        Double quantity = 1.0;
        Double stopPrice = 9500.0;
        Long cancelOrderId = 1l;
        when(this.binanceAPI.cancelAndNewOrderWithStopLoss(symbol, price, quantity, stopPrice, cancelOrderId))
                .thenThrow(new RuntimeException("API call failed"));

        // Act & Assert
        assertThrows(Exception.class, () -> this.binanceApiManager.cancelAndNewOrderWithStopLoss(symbol, price, quantity, stopPrice, cancelOrderId));
        verify(this.binanceAPI)
                .cancelAndNewOrderWithStopLoss(symbol, price, quantity, stopPrice, cancelOrderId);
        verify(this.binanceOrderService, never())
                .createCancelAndNewOrderResponse(any(CancelAndNewOrderResponse.class));
    }

    @Test
    void testNewOrder_Success() throws Exception {
        // Arrange
        String symbol = "BTCUSDT";
        Double price = 10000.0;
        Double quantity = 1.0;
        String orderResponseJson = TestData.ORDER_RESPONSE;
        when(this.binanceAPI.newOrder(symbol, price, quantity))
                .thenReturn(orderResponseJson);
        when(this.binanceOrderService.createOrderResponse(any(OrderResponse.class)))
                .thenAnswer((Answer<OrderResponse>) invocation -> (OrderResponse) invocation.getArguments()[0]);

        // Act
        OrderResponse result = this.binanceApiManager.newOrder(symbol, price, quantity);

        // Assert
        verify(this.binanceAPI)
                .newOrder(symbol, price, quantity);
        verify(this.binanceOrderService)
                .createOrderResponse(any(OrderResponse.class));
        assertNotNull(result);
    }

    @Test
    void testNewOrder_ApiException() {
        // Arrange
        String symbol = "BTCUSDT";
        Double price = 10000.0;
        Double quantity = 1.0;
        when(this.binanceAPI.newOrder(symbol, price, quantity))
                .thenThrow(new RuntimeException("API call failed"));

        // Act & Assert
        assertThrows(Exception.class, () -> this.binanceApiManager.newOrder(symbol, price, quantity));
        verify(this.binanceAPI)
                .newOrder(symbol, price, quantity);
        verify(this.binanceOrderService, never())
                .createOrderResponse(any(OrderResponse.class));
    }
}
