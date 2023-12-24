package com.ozgen.telegrambinancebot.utils.parser;


import com.ozgen.telegrambinancebot.model.binance.CancelAndNewOrderResponse;
import com.ozgen.telegrambinancebot.model.binance.OpenOrder;
import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
import com.ozgen.telegrambinancebot.model.binance.OrderResponse;
import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {

    @Test
    void testJsonObjParsing_withValidJson() throws Exception {
        String json = "{ \"symbol\": \"BNBBTC\", \"openPrice\": \"99.00000000\", \"highPrice\": \"100.00000000\", \"lowPrice\": \"0.10000000\", \"lastPrice\": \"4.00000200\", \"volume\": \"8913.30000000\", \"quoteVolume\": \"15.30000000\", \"openTime\": 1499783499040, \"closeTime\": 1499869899040, \"firstId\": 28385, \"lastId\": 28460, \"count\": 76 }";
        TickerData tickerData = JsonParser.parseTickerJson(json);

        assertNotNull(tickerData);
        assertEquals("BNBBTC", tickerData.getSymbol());
        assertEquals("99.00000000", tickerData.getOpenPrice());
        assertEquals(28385, tickerData.getFirstId());
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

    @Test
    public void testParseSnapshotJson_withValidJson() throws Exception {
        String jsonString = "{\"code\":200,\"msg\":\"\",\"snapshotVos\":[{\"type\":\"spot\",\"updateTime\":1700351999000,\"data\":{\"totalAssetOfBtc\":\"0.00270573\",\"balances\":[{\"asset\":\"ADX\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"ATM\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"AUDIO\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"BCHSV\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"BNB\",\"free\":\"0.25760623\",\"locked\":\"0\"},{\"asset\":\"BTC\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"BTS\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"BUSD\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"CHESS\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"CTSI\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"DATA\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"DGB\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"DOCK\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"DUSK\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"FOR\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"FTM\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"FUN\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"GALA\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"IDEX\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"IOTX\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"KEY\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"KMD\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"MBOX\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"PEOPLE\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"REEF\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"SALT\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"SC\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"TRIBE\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"USDT\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"WAVES\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"WAXP\",\"free\":\"516.483\",\"locked\":\"0\"},{\"asset\":\"XRP\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"XVG\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"ZRX\",\"free\":\"0\",\"locked\":\"0\"}]}}]}";

        SnapshotData result = JsonParser.parseSnapshotJson(jsonString);

        assertNotNull(result);
        assertNotNull(result.getSnapshotVos());
        assertFalse(result.getSnapshotVos().isEmpty());

        SnapshotData.SnapshotVo firstSnapshot = result.getSnapshotVos().get(0);
        assertEquals("spot", firstSnapshot.getType());
        assertNotNull(firstSnapshot.getData());
        assertEquals("0.00270573", firstSnapshot.getData().getTotalAssetOfBtc());

        List<SnapshotData.Balance> balances = firstSnapshot.getData().getBalances();
        assertNotNull(balances);
        assertFalse("Balances should not be empty", balances.isEmpty());

        SnapshotData.Balance firstBalance = balances.get(0);
        assertEquals("ADX", firstBalance.getAsset());
        assertEquals("0", firstBalance.getFree());
        assertEquals("0", firstBalance.getLocked());

    }

    @Test
    public void testParseOpenInfoJson_withValidJson() throws Exception {
        // Arrange
        String jsonString = "[" +
                "{" +
                "\"symbol\": \"LTCBTC\"," +
                "\"orderId\": 1," +
                "\"orderListId\": -1," +
                "\"clientOrderId\": \"myOrder1\"," +
                "\"price\": \"0.1\"," +
                "\"origQty\": \"1.0\"," +
                "\"executedQty\": \"0.0\"," +
                "\"cummulativeQuoteQty\": \"0.0\"," +
                "\"status\": \"NEW\"," +
                "\"timeInForce\": \"GTC\"," +
                "\"type\": \"LIMIT\"," +
                "\"side\": \"BUY\"," +
                "\"stopPrice\": \"0.0\"," +
                "\"icebergQty\": \"0.0\"," +
                "\"time\": 1499827319559," +
                "\"updateTime\": 1499827319559," +
                "\"isWorking\": true," +
                "\"origQuoteOrderQty\": \"0.000000\"," +
                "\"workingTime\": 1499827319559," +
                "\"selfTradePreventionMode\": \"NONE\"" +
                "}" +
                "]";

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
        String jsonString = "["
                + "{"
                + "\"symbol\": \"BTCUSDT\","
                + "\"origClientOrderId\": \"E6APeyTJvkMvLMYMqu1KQ4\","
                + "\"orderId\": 11,"
                + "\"orderListId\": -1,"
                + "\"clientOrderId\": \"pXLV6Hz6mprAcVYpVMTGgx\","
                + "\"transactTime\": 1684804350068,"
                + "\"price\": \"0.089853\","
                + "\"origQty\": \"0.178622\","
                + "\"executedQty\": \"0.000000\","
                + "\"cummulativeQuoteQty\": \"0.000000\","
                + "\"status\": \"CANCELED\","
                + "\"timeInForce\": \"GTC\","
                + "\"type\": \"LIMIT\","
                + "\"side\": \"BUY\","
                + "\"selfTradePreventionMode\": \"NONE\""
                + "},"
                + "{"
                + "\"symbol\": \"BTCUSDT\","
                + "\"origClientOrderId\": \"A3EF2HCwxgZPFMrfwbgrhv\","
                + "\"orderId\": 13,"
                + "\"orderListId\": -1,"
                + "\"clientOrderId\": \"pXLV6Hz6mprAcVYpVMTGgx\","
                + "\"transactTime\": 1684804350069,"
                + "\"price\": \"0.090430\","
                + "\"origQty\": \"0.178622\","
                + "\"executedQty\": \"0.000000\","
                + "\"cummulativeQuoteQty\": \"0.000000\","
                + "\"status\": \"CANCELED\","
                + "\"timeInForce\": \"GTC\","
                + "\"type\": \"LIMIT\","
                + "\"side\": \"BUY\","
                + "\"selfTradePreventionMode\": \"NONE\""
                + "},"
                + "{"
                + "\"orderListId\": 1929,"
                + "\"contingencyType\": \"OCO\","
                + "\"listStatusType\": \"ALL_DONE\","
                + "\"listOrderStatus\": \"ALL_DONE\","
                + "\"listClientOrderId\": \"2inzWQdDvZLHbbAmAozX2N\","
                + "\"transactionTime\": 1585230948299,"
                + "\"symbol\": \"BTCUSDT\","
                + "\"orders\": ["
                + "    {"
                + "    \"symbol\": \"BTCUSDT\","
                + "    \"orderId\": 20,"
                + "    \"clientOrderId\": \"CwOOIPHSmYywx6jZX77TdL\""
                + "    },"
                + "    {"
                + "    \"symbol\": \"BTCUSDT\","
                + "    \"orderId\": 21,"
                + "    \"clientOrderId\": \"461cPg51vQjV3zIMOXNz39\""
                + "    }"
                + "],"
                + "\"orderReports\": ["
                + "    {"
                + "    \"symbol\": \"BTCUSDT\","
                + "    \"origClientOrderId\": \"CwOOIPHSmYywx6jZX77TdL\","
                + "    \"orderId\": 20,"
                + "    \"orderListId\": 1929,"
                + "    \"clientOrderId\": \"pXLV6Hz6mprAcVYpVMTGgx\","
                + "    \"transactTime\": 1688005070874,"
                + "    \"price\": \"0.668611\","
                + "    \"origQty\": \"0.690354\","
                + "    \"executedQty\": \"0.000000\","
                + "    \"cummulativeQuoteQty\": \"0.000000\","
                + "    \"status\": \"CANCELED\","
                + "    \"timeInForce\": \"GTC\","
                + "    \"type\": \"STOP_LOSS_LIMIT\","
                + "    \"side\": \"BUY\","
                + "    \"stopPrice\": \"0.378131\","
                + "    \"icebergQty\": \"0.017083\","
                + "    \"selfTradePreventionMode\": \"NONE\""
                + "    },"
                + "    {"
                + "    \"symbol\": \"BTCUSDT\","
                + "    \"origClientOrderId\": \"461cPg51vQjV3zIMOXNz39\","
                + "    \"orderId\": 21,"
                + "    \"orderListId\": 1929,"
                + "    \"clientOrderId\": \"pXLV6Hz6mprAcVYpVMTGgx\","
                + "    \"transactTime\": 1688005070874,"
                + "    \"price\": \"0.008791\","
                + "    \"origQty\": \"0.690354\","
                + "    \"executedQty\": \"0.000000\","
                + "    \"cummulativeQuoteQty\": \"0.000000\","
                + "    \"status\": \"CANCELED\","
                + "    \"timeInForce\": \"GTC\","
                + "    \"type\": \"LIMIT_MAKER\","
                + "    \"side\": \"BUY\","
                + "    \"icebergQty\": \"0.639962\","
                + "    \"selfTradePreventionMode\": \"NONE\""
                + "    }"
                + "]"
                + "}"
                + "]";

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
        String jsonString = "{"
                + "\"cancelResult\": \"SUCCESS\","
                + "\"newOrderResult\": \"SUCCESS\","
                + "\"cancelResponse\": {"
                + "    \"symbol\": \"BTCUSDT\","
                + "    \"origClientOrderId\": \"DnLo3vTAQcjha43lAZhZ0y\","
                + "    \"orderId\": 9,"
                + "    \"orderListId\": -1,"
                + "    \"clientOrderId\": \"osxN3JXAtJvKvCqGeMWMVR\","
                + "    \"transactTime\": 1684804350068,"
                + "    \"price\": \"0.01000000\","
                + "    \"origQty\": \"0.000100\","
                + "    \"executedQty\": \"0.00000000\","
                + "    \"cummulativeQuoteQty\": \"0.00000000\","
                + "    \"status\": \"CANCELED\","
                + "    \"timeInForce\": \"GTC\","
                + "    \"type\": \"LIMIT\","
                + "    \"side\": \"SELL\","
                + "    \"selfTradePreventionMode\": \"NONE\""
                + "},"
                + "\"newOrderResponse\": {"
                + "    \"symbol\": \"BTCUSDT\","
                + "    \"orderId\": 10,"
                + "    \"orderListId\": -1,"
                + "    \"clientOrderId\": \"wOceeeOzNORyLiQfw7jd8S\","
                + "    \"transactTime\": 1652928801803,"
                + "    \"price\": \"0.02000000\","
                + "    \"origQty\": \"0.040000\","
                + "    \"executedQty\": \"0.00000000\","
                + "    \"cummulativeQuoteQty\": \"0.00000000\","
                + "    \"status\": \"NEW\","
                + "    \"timeInForce\": \"GTC\","
                + "    \"type\": \"LIMIT\","
                + "    \"side\": \"BUY\","
                + "    \"workingTime\": 1669277163808,"
                + "    \"fills\": [],"
                + "    \"selfTradePreventionMode\": \"NONE\""
                + "}"
                + "}";
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
        String jsonString = "{"
                + "\"symbol\": \"BTCUSDT\","
                + "\"orderId\": 28,"
                + "\"orderListId\": -1,"
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
                + "\"selfTradePreventionMode\": \"NONE\","
                + "\"fills\": ["
                + "    {"
                + "        \"price\": \"4000.00000000\","
                + "        \"qty\": \"1.00000000\","
                + "        \"commission\": \"4.00000000\","
                + "        \"commissionAsset\": \"USDT\","
                + "        \"tradeId\": 56"
                + "    },"
                + "    {"
                + "        \"price\": \"3999.00000000\","
                + "        \"qty\": \"5.00000000\","
                + "        \"commission\": \"19.99500000\","
                + "        \"commissionAsset\": \"USDT\","
                + "        \"tradeId\": 57"
                + "    },"
                + "    {"
                + "        \"price\": \"3998.00000000\","
                + "        \"qty\": \"2.00000000\","
                + "        \"commission\": \"7.99600000\","
                + "        \"commissionAsset\": \"USDT\","
                + "        \"tradeId\": 58"
                + "    },"
                + "    {"
                + "        \"price\": \"3997.00000000\","
                + "        \"qty\": \"1.00000000\","
                + "        \"commission\": \"3.99700000\","
                + "        \"commissionAsset\": \"USDT\","
                + "        \"tradeId\": 59"
                + "    },"
                + "    {"
                + "        \"price\": \"3995.00000000\","
                + "        \"qty\": \"1.00000000\","
                + "        \"commission\": \"3.99500000\","
                + "        \"commissionAsset\": \"USDT\","
                + "        \"tradeId\": 60"
                + "    }"
                + "]"
                + "}";
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
