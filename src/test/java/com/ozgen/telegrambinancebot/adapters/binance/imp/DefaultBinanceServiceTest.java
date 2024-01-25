package com.ozgen.telegrambinancebot.adapters.binance.imp;


import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.spot.Market;
import com.binance.connector.client.impl.spot.Trade;
import com.binance.connector.client.impl.spot.Wallet;
import com.ozgen.telegrambinancebot.utils.parser.GenericParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultBinanceServiceTest {
    @Mock
    private SpotClient binanceClient;
    @Mock
    private Wallet wallet;
    @Mock
    private Trade trade;
    @Mock
    private Market market;

    @InjectMocks
    private DefaultBinanceService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.service = new DefaultBinanceService(this.binanceClient);

        when(this.binanceClient.createWallet())
                .thenReturn(this.wallet);
        when(this.binanceClient.createTrade())
                .thenReturn(this.trade);
        when(this.binanceClient.createMarket())
                .thenReturn(this.market);
    }

    @Test
    void testGetBTCWalletStatus() {
        String expectedResponse = "wallet_status_response";
        when(this.wallet.accountStatus(any()))
                .thenReturn(expectedResponse);

        String actualResponse = service.getBTCWalletStatus();

        assertEquals(expectedResponse, actualResponse);
        verify(binanceClient.createWallet())
                .accountStatus(any());
    }

    @Test
    void testGetAccountSnapshot() {
        String expectedResponse = "account_snapshot_response";
        when(binanceClient.createWallet().accountSnapshot(anyMap()))
                .thenReturn(expectedResponse);

        String actualResponse = service.getAccountSnapshot();

        assertEquals(expectedResponse, actualResponse);
        verify(binanceClient.createWallet())
                .accountSnapshot(anyMap());
    }

    @Test
    void testCancelOpenOrders() {
        String symbol = "BTCUSDT";
        String expectedResponse = "cancel_order_response";
        when(binanceClient.createTrade().cancelOpenOrders(anyMap()))
                .thenReturn(expectedResponse);

        String actualResponse = service.cancelOpenOrders(symbol);

        assertEquals(expectedResponse, actualResponse);
        verify(binanceClient.createTrade()).cancelOpenOrders(argThat(params ->
                params.containsKey("symbol") && params.get("symbol").equals(symbol)));
    }

    @Test
    void testCancelAndNewOrderWithStopLoss() {
        String symbol = "BTCUSDT";
        Double price = 10000.0;
        Double quantity = 1.0;
        Long cancelOrderId = 12345L;
        String expectedResponse = "cancel_and_new_order_response";
        when(binanceClient.createTrade().cancelReplace(anyMap()))
                .thenReturn(expectedResponse);

        String actualResponse = service.cancelAndNewOrder(symbol, price, quantity, cancelOrderId);

        assertEquals(expectedResponse, actualResponse);
        verify(binanceClient.createTrade())
                .cancelReplace(argThat(params ->
                        params.containsKey("symbol") && params.get("symbol").equals(symbol) &&
                                params.get("side").equals("BUY") &&
                                params.get("type").equals("LIMIT") &&
                                params.get("timeInForce").equals("GTC") &&
                                params.get("quantity").equals(GenericParser.getFormattedDouble(quantity)) &&
                                params.get("price").equals(GenericParser.getFormattedDouble(price)) &&
                                params.get("cancelReplaceMode").equals("STOP_ON_FAILURE") &&
                                params.get("cancelOrderId").equals(cancelOrderId)));
    }

    @Test
    void testNewOrder() {
        String symbol = "BTCUSDT";
        Double price = 50000.0;
        Double quantity = 0.5;
        String expectedResponse = "new_order_response";

        when(binanceClient.createTrade().newOrder(anyMap()))
                .thenReturn(expectedResponse);

        String actualResponse = service.newOrder(symbol, price, quantity);

        assertEquals(expectedResponse, actualResponse);
        verify(binanceClient.createTrade())
                .newOrder(argThat(params ->
                        "BTCUSDT".equals(params.get("symbol")) &&
                                "BUY".equals(params.get("side")) &&
                                "LIMIT".equals(params.get("type")) &&
                                "GTC".equals(params.get("timeInForce")) &&
                                price.equals(params.get("price")) &&
                                quantity.equals(params.get("quantity"))
                ));
    }

    @Test
    void testGetTickerPrice24() {
        String symbol = "BTCUSDT";
        String expectedResponse = "ticker_price_24h_response";

        when(binanceClient.createMarket().ticker24H(anyMap()))
                .thenReturn(expectedResponse);

        String actualResponse = service.getTickerPrice24(symbol);

        assertEquals(expectedResponse, actualResponse);
        verify(binanceClient.createMarket()).ticker24H(argThat(params ->
                "BTCUSDT".equals(params.get("symbol")) &&
                        "MINI".equals(params.get("type"))
        ));
    }

    @Test
    void testGetCoinPrice() {
        String symbol = "BTCUSDT";
        String expectedResponse = "coin_price_response";

        when(binanceClient.createMarket().averagePrice(anyMap())).thenReturn(expectedResponse);

        String actualResponse = service.getCoinPrice(symbol);

        assertEquals(expectedResponse, actualResponse);
        verify(binanceClient.createMarket()).averagePrice(argThat(params ->
                "BTCUSDT".equals(params.get("symbol"))
        ));
    }

    @Test
    void testNewOrderWithStopLoss() {
        String symbol = "BTCUSDT";
        Double price = 10000.0;
        Double quantity = 1.0;
        Double stopPrice = 9500.0;
        String expectedResponse = "order_response";

        when(binanceClient.createTrade().newOrder(anyMap()))
                .thenReturn(expectedResponse);

        String actualResponse = service.newOrderWithStopLoss(symbol, price, quantity, stopPrice);

        assertEquals(expectedResponse, actualResponse);
        verify(binanceClient.createTrade())
                .newOrder(argThat(params ->
                        "BTCUSDT".equals(params.get("symbol")) &&
                                "SELL".equals(params.get("side")) &&
                                "STOP_LOSS_LIMIT".equals(params.get("type")) &&
                                "GTC".equals(params.get("timeInForce")) &&                                params.get("quantity").equals(GenericParser.getFormattedDouble(quantity)) &&
                                params.get("price").equals(GenericParser.getFormattedDouble(price)) &&
                                params.get("stopPrice").equals(GenericParser.getFormattedDouble(stopPrice))
                ));
    }

    @Test
    void testGetOpenOrders() {
        String symbol = "BTCUSDT";
        String expectedResponse = "open_orders_response";

        when(binanceClient.createTrade().getOpenOrders(anyMap()))
                .thenReturn(expectedResponse);

        String actualResponse = service.getOpenOrders(symbol);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testGetUserAssets() {
        String expectedResponse = "open_orders_response";

        when(binanceClient.createWallet().getUserAsset(anyMap()))
                .thenReturn(expectedResponse);

        String actualResponse = service.getUserAsset();

        assertEquals(expectedResponse, actualResponse);
    }
}
