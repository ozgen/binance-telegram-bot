package com.ozgen.telegrambinancebot.utils.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ozgen.telegrambinancebot.model.binance.AssetBalance;
import com.ozgen.telegrambinancebot.model.binance.CancelAndNewOrderResponse;
import com.ozgen.telegrambinancebot.model.binance.KlineData;
import com.ozgen.telegrambinancebot.model.binance.OpenOrder;
import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
import com.ozgen.telegrambinancebot.model.binance.OrderResponse;
import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static TickerData parseTickerJson(String jsonString) throws Exception {
        if (isJsonArray(jsonString)) {
            return parseTickerJsonArray(jsonString);
        } else if (isJsonObject(jsonString)) {
            return parseTickerJsonObj(jsonString);
        }
        return null;
    }

    public static SnapshotData parseSnapshotJson(String jsonString) throws Exception {
        return mapper.readValue(jsonString, SnapshotData.class);
    }

    public static List<OpenOrder> parseOpenOrdersJson(String jsonString) throws Exception {
        return mapper.readValue(jsonString, new TypeReference<List<OpenOrder>>() {
        });
    }

    public static List<OrderInfo> parseOrderInfoJson(String jsonString) throws Exception {
        return mapper.readValue(jsonString, new TypeReference<List<OrderInfo>>() {
        });
    }

    public static List<AssetBalance> parseAssetBalanceJson(String jsonString) throws Exception {
        return mapper.readValue(jsonString, new TypeReference<List<AssetBalance>>() {
        });
    }

    public static OrderResponse parseOrderResponseJson(String jsonString) throws Exception {
        return mapper.readValue(jsonString, OrderResponse.class);
    }

    public static CancelAndNewOrderResponse parseCancelAndNewOrderResponseJson(String jsonString) throws Exception {
        return mapper.readValue(jsonString, CancelAndNewOrderResponse.class);
    }

    public static TickerData parseTickerJsonObj(String jsonString) throws Exception {
        return mapper.readValue(jsonString, TickerData.class);
    }

    public static TickerData parseTickerJsonArray(String jsonString) throws Exception {
        List<TickerData> tickerDataList = mapper.readValue(jsonString, new TypeReference<List<TickerData>>() {
        });
        if (tickerDataList.isEmpty()) {
            return null;
        }
        return tickerDataList.get(0);
    }

    public static boolean isJsonArray(String json) {
        return json.trim().startsWith("[");
    }

    public static boolean isJsonObject(String json) {
        return json.trim().startsWith("{");
    }

    public static List<KlineData> parseKlinesJson(String jsonString) {
        JSONArray klinesArray = new JSONArray(jsonString);
        List<KlineData> klinesList = new ArrayList<>();

        for (int i = 0; i < klinesArray.length(); i++) {
            JSONArray klineArray = klinesArray.getJSONArray(i);

            long openTime = klineArray.getLong(0);
            String openPrice = klineArray.getString(1);
            String highPrice = klineArray.getString(2);
            String lowPrice = klineArray.getString(3);
            String closePrice = klineArray.getString(4);
            String volume = klineArray.getString(5);
            long closeTime = klineArray.getLong(6);
            double quoteAssetVolume = klineArray.getDouble(7);
            int numberOfTrades = klineArray.getInt(8);
            String takerBuyBaseAssetVolume = klineArray.getString(9);
            String takerBuyQuoteAssetVolume = klineArray.getString(10);

            // Create a new KlineData object and add it to the list
            KlineData klineData = new KlineData(openTime, openPrice, highPrice, lowPrice, closePrice, volume,
                    closeTime, quoteAssetVolume, numberOfTrades,
                    takerBuyBaseAssetVolume, takerBuyQuoteAssetVolume);

            klinesList.add(klineData);
        }

        return klinesList;
    }
}
