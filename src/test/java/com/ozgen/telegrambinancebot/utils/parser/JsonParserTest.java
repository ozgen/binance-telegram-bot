package com.ozgen.telegrambinancebot.utils.parser;


import com.ozgen.telegrambinancebot.model.binance.AssetBalance;
import com.ozgen.telegrambinancebot.model.binance.CancelAndNewOrderResponse;
import com.ozgen.telegrambinancebot.model.binance.OpenOrder;
import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
import com.ozgen.telegrambinancebot.model.binance.OrderResponse;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.utils.TestData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonParserTest {

    @Test
    void testJsonObjParsing_withValidJson() throws Exception {
        String json = TestData.TICKER_DATA;
        TickerData tickerData = JsonParser.parseTickerJson(json);

        assertNotNull(tickerData);
        assertEquals("BNBBTC", tickerData.getSymbol());
        assertEquals("99.00000000", tickerData.getOpenPrice());
        assertEquals(28385, tickerData.getFirstId());
    }

    @Test
    void testJsonAssetsParsing_withValidJson() throws Exception {
        String json = TestData.ASSETS_DATA;
        List<AssetBalance> assets = JsonParser.parseAssetBalanceJson(json);

        assertNotNull(assets);
    }

    @Test
    void testJsonArrayParsing_withValidJson() throws Exception {
        String json = "[{ \"symbol\": \"BNBBTC\", \"openPrice\": \"99.00000000\", \"highPrice\": \"100.00000000\", \"lowPrice\": \"0.10000000\", \"lastPrice\": \"4.00000200\", \"volume\": \"8913.30000000\", \"quoteVolume\": \"15.30000000\", \"openTime\": 1499783499040, \"closeTime\": 1499869899040, \"firstId\": 28385, \"lastId\": 28460, \"count\": 76 }]";
        TickerData tickerData = JsonParser.parseTickerJson(json);

        assertNotNull(tickerData);
        assertEquals("BNBBTC", tickerData.getSymbol());
        assertEquals("99.00000000", tickerData.getOpenPrice());
        assertEquals(28385, tickerData.getFirstId());
    }

//    @Test
//    public void testParseSnapshotJson_withValidJson() throws Exception {
//        String jsonString = TestData.SNAPSHOT_DATA;
//
//        SnapshotData result = JsonParser.parseSnapshotJson(jsonString);
//
//        assertNotNull(result);
//        assertNotNull(result.getSnapshotVos());
//        assertFalse(result.getSnapshotVos().isEmpty());
//
//        SnapshotData.SnapshotVo firstSnapshot = result.getSnapshotVos().get(0);
//        assertEquals("spot", firstSnapshot.getType());
//        assertNotNull(firstSnapshot.getData());
//        assertEquals("0.00270573", firstSnapshot.getData().getTotalAssetOfBtc());
//
//        List<SnapshotData.Balance> balances = firstSnapshot.getData().getBalances();
//        assertNotNull(balances);
//        assertFalse( balances.isEmpty());
//
//        SnapshotData.Balance firstBalance = balances.get(0);
//        assertEquals("ADX", firstBalance.getAsset());
//        assertEquals("0", firstBalance.getFree());
//        assertEquals("0", firstBalance.getLocked());
//
//    }

    @Test
    public void testParseOpenInfoJson_withValidJson() throws Exception {
        // Arrange
        String jsonString = TestData.ORDER_INFO_ARRAY;

        // Act
        List<OrderInfo> result = JsonParser.parseOrderInfoJson(jsonString);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        OrderInfo order = result.get(0);
        assertNotNull(order);
        assertEquals("LTCBTC", order.getSymbol());
        assertEquals(1, order.getOrderId());
    }

    @Test
    public void testParseOpenOrdersJson_withValidJson() throws Exception {
        // Arrange
        String jsonString = TestData.OPEN_ORDER_ARRAY;

        // Act
        List<OpenOrder> result = JsonParser.parseOpenOrdersJson(jsonString);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Test the first order
        OpenOrder firstOrder = result.get(0);
        assertNotNull(firstOrder);
        assertEquals("BTCUSDT", firstOrder.getSymbol());
        assertEquals(Long.valueOf(11), firstOrder.getOrderId());

        OpenOrder thirdOrder = result.get(2);
        assertNotNull(thirdOrder);
    }


    @Test
    public void testParseCancelAndNewOrderResponseJson_withValidJson() throws Exception {
        // Arrange
        String jsonString = TestData.CANCEL_AND_NEW_ORDER_RESPONSE;
        // Act
        CancelAndNewOrderResponse result = JsonParser.parseCancelAndNewOrderResponseJson(jsonString);

        // Assert
        assertNotNull(result);
        assertEquals("SUCCESS", result.getCancelResult());
        assertEquals("SUCCESS", result.getNewOrderResult());
        assertNotNull(result.getCancelResponse());
        assertNotNull(result.getNewOrderResponse());
    }

    @Test
    public void testParseOrderResponseJson_withValidFullData() throws Exception {
        //arrange
        String jsonString = TestData.ORDER_RESPONSE;
        // Act
        OrderResponse result = JsonParser.parseOrderResponseJson(jsonString);

        // Assert
        assertNotNull(result);
        assertEquals("BTCUSDT", result.getSymbol());
        assertEquals(Long.valueOf(28), result.getOrderId());

        assertNotNull(result.getFills());
        assertFalse(result.getFills().isEmpty());
    }

    @Test
    public void testParseOrderResponseJson_withValidData() throws Exception {
        //arrange
        String jsonString = "{"
                + "\"symbol\": \"BTCUSDT\","
                + "\"orderId\": 28,"
                + "\"orderListId\": -1," // Note: Comments like this one in JSON are not typical and might need to be removed or handled specifically in Java
                + "\"clientOrderId\": \"6gCrw2kRUAF9CvJDGP16IP\","
                + "\"transactTime\": 1507725176595,"
                + "\"price\": \"0.00000000\","
                + "\"origQty\": \"10.00000000\","
                + "\"executedQty\": \"10.00000000\","
                + "\"cummulativeQuoteQty\": \"10.00000000\","
                + "\"status\": \"FILLED\","
                + "\"timeInForce\": \"GTC\","
                + "\"type\": \"MARKET\","
                + "\"side\": \"SELL\","
                + "\"workingTime\": 1507725176595,"
                + "\"selfTradePreventionMode\": \"NONE\""
                + "}";

        // Act
        OrderResponse result = JsonParser.parseOrderResponseJson(jsonString);

        // Assert
        assertNotNull(result);
        assertEquals("BTCUSDT", result.getSymbol());
        assertEquals(Long.valueOf(28), result.getOrderId());
        assertTrue(result.getFills() == null);
    }
}
