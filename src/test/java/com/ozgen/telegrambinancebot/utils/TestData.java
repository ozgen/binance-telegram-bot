package com.ozgen.telegrambinancebot.utils;

import com.ozgen.telegrambinancebot.model.binance.AssetBalance;
import com.ozgen.telegrambinancebot.model.binance.CancelAndNewOrderResponse;
import com.ozgen.telegrambinancebot.model.binance.OpenOrder;
import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
import com.ozgen.telegrambinancebot.model.binance.OrderResponse;
import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.utils.parser.JsonParser;
import com.ozgen.telegrambinancebot.utils.parser.SignalParser;

import java.util.List;

public class TestData {

    public static String SNAPSHOT_DATA = "{\"code\":200,\"msg\":\"\",\"snapshotVos\":[{\"type\":\"spot\",\"updateTime\":1700351999000,\"data\":{\"totalAssetOfBtc\":\"0.00270573\",\"balances\":[{\"asset\":\"ADX\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"ATM\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"AUDIO\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"BCHSV\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"BNB\",\"free\":\"0.25760623\",\"locked\":\"0\"},{\"asset\":\"BTC\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"BTS\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"BUSD\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"CHESS\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"CTSI\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"DATA\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"DGB\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"DOCK\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"DUSK\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"FOR\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"FTM\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"FUN\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"GALA\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"IDEX\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"IOTX\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"KEY\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"KMD\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"MBOX\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"PEOPLE\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"REEF\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"SALT\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"SC\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"TRIBE\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"USDT\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"WAVES\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"WAXP\",\"free\":\"516.483\",\"locked\":\"0\"},{\"asset\":\"XRP\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"XVG\",\"free\":\"0\",\"locked\":\"0\"},{\"asset\":\"ZRX\",\"free\":\"0\",\"locked\":\"0\"}]}}]}";

    public static String ASSETS_DATA = "[{\"asset\":\"BNB\",\"free\":\"0.00004272\",\"locked\":\"0\",\"freeze\":\"0\",\"withdrawing\":\"0\",\"ipoable\":\"0\",\"btcValuation\":\"0\"},{\"asset\":\"BTC\",\"free\":\"0.00131558\",\"locked\":\"0.0025008\",\"freeze\":\"0\",\"withdrawing\":\"0\",\"ipoable\":\"0\",\"btcValuation\":\"0\"},{\"asset\":\"GO\",\"free\":\"0.278\",\"locked\":\"0\",\"freeze\":\"0\",\"withdrawing\":\"0\",\"ipoable\":\"0\",\"btcValuation\":\"0\"},{\"asset\":\"NFT\",\"free\":\"3230135.199035\",\"locked\":\"0\",\"freeze\":\"0\",\"withdrawing\":\"0\",\"ipoable\":\"0\",\"btcValuation\":\"0\"},{\"asset\":\"PEPE\",\"free\":\"0.88\",\"locked\":\"0\",\"freeze\":\"0\",\"withdrawing\":\"0\",\"ipoable\":\"0\",\"btcValuation\":\"0\"},{\"asset\":\"POND\",\"free\":\"5317752.55704\",\"locked\":\"0\",\"freeze\":\"0\",\"withdrawing\":\"0\",\"ipoable\":\"0\",\"btcValuation\":\"0\"},{\"asset\":\"USDT\",\"free\":\"0.04207198\",\"locked\":\"0\",\"freeze\":\"0\",\"withdrawing\":\"0\",\"ipoable\":\"0\",\"btcValuation\":\"0\"}]";

    public static String TICKER_DATA = "{ \"symbol\": \"BNBBTC\", \"openPrice\": \"99.00000000\", \"highPrice\": \"100.00000000\", \"lowPrice\": \"0.10000000\", \"lastPrice\": \"4.00000200\", \"volume\": \"8913.30000000\", \"quoteVolume\": \"15.30000000\", \"openTime\": 1499783499040, \"closeTime\": 1499869899040, \"firstId\": 28385, \"lastId\": 28460, \"count\": 76 }";
    public static String ORDER_INFO_ARRAY = "[" +
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
    public static String ORDER_INFO_ARRAY_SELL = "[" +
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
            "\"side\": \"SELL\"," +
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

    public static String OPEN_ORDER_ARRAY = "["
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

    public static String CANCEL_AND_NEW_ORDER_RESPONSE = "{"
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

    public static String ORDER_RESPONSE = "{"
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

    public static String TRADING_SIGNAL = "NKNBTC\n\nENTRY: 0.00000260 - 0.00000290\n\nTP1: 0.00000315\nTP2: 0.00000360\nTP3: 0.00000432\nTP4: 0.00000486\nTP5: 0.00000550\nTP6: 0.00000666\nTP7: 0.00000741\n\nSTOP: Close weekly below 0.00000240\n\nSHARED: 18-Nov-2023 @lamatrades ✨";
    public static String TRADING_SIGNAL_2 = "ATABTC\n\nENTRY: 0.00000240 - 0.00000280\n\nSTOP: Close weekly below 0.00000220\n\nSHARED: 31-Dec-2023";
    public static String TRADING_SIGNAL_3 = "MDTBTC\n" +
            "\n" +
            "ENTRY: 0.00000125 - 0.00000150\n" +
            "\n" +
            "\n" +
            "STOP: Close weekly  below 0.00000120";
    public static String TRADING_SIGNAL_4 = "MDTBTC\n" +
            "\n" +
            "ENTRY: 0.00000125 - 0.00000150\n" +
            "\n" +
            "STOP: 0.00000120";
    public static String TRADING_SIGNAL_SELL_LATER = "MDTBTC\n" +
            "\n" +
            "ENTRY: 0.00000125 - 0.00000150\n" +
            "INVEST: 0.0001 \n" +
            "STRATEGY: SELL_LATER \n" +
            "\n" +
            "STOP: 0.00000120";
    public static String INVALID_TRADING_SIGNAL = "NKNBTC\n\nTP1: 0.00000315\nTP2: 0.00000360\nTP3: 0.00000432\nTP4: 0.00000486\nTP5: 0.00000550\nTP6: 0.00000666\nTP7: 0.00000741\n\nSTOP: Close weekly below 0.00000240\n\nSHARED: 18-Nov-2023 @lamatrades ✨";


    public static TradingSignal getTradingSignal() {
        return SignalParser.parseSignal(TRADING_SIGNAL);
    }
    public static TradingSignal getTradingSignalSellLater() {
        return SignalParser.parseSignal(TRADING_SIGNAL_SELL_LATER);
    }

    public static CancelAndNewOrderResponse getCancelAndNewOrderResponse() throws Exception {
        return JsonParser.parseCancelAndNewOrderResponseJson(CANCEL_AND_NEW_ORDER_RESPONSE);
    }

    public static List<OpenOrder> getOpenOrders() throws Exception {
        return JsonParser.parseOpenOrdersJson(OPEN_ORDER_ARRAY);
    }

    public static List<OrderInfo> getOrderInfos() throws Exception {
        return JsonParser.parseOrderInfoJson(ORDER_INFO_ARRAY);
    }

    public static OrderResponse getOrderResponse() throws Exception {
        return JsonParser.parseOrderResponseJson(ORDER_RESPONSE);
    }

    public static SnapshotData getSnapshotData() throws Exception {
        return JsonParser.parseSnapshotJson(SNAPSHOT_DATA);
    }

    public static TickerData getTickerData() throws Exception {
        return JsonParser.parseTickerJson(TICKER_DATA);
    }

    public static List<AssetBalance> getAssets() throws Exception {
        return JsonParser.parseAssetBalanceJson(ASSETS_DATA);
    }
}
