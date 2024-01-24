package com.ozgen.telegrambinancebot.utils.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ozgen.telegrambinancebot.model.binance.AssetBalance;
import com.ozgen.telegrambinancebot.model.binance.CancelAndNewOrderResponse;
import com.ozgen.telegrambinancebot.model.binance.OpenOrder;
import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
import com.ozgen.telegrambinancebot.model.binance.OrderResponse;
import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
import com.ozgen.telegrambinancebot.model.binance.TickerData;

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
}
