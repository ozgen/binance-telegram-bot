package com.ozgen.telegrambinancebot.adapters.binance.imp;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;
import com.ozgen.telegrambinancebot.adapters.binance.BinanceAPI;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DefaultBinanceService implements BinanceAPI {

    private final SpotClient binanceClient;

    public DefaultBinanceService(SpotClientImpl spotClient) {
        this.binanceClient = spotClient;
    }

    public String getBTCWalletStatus() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        return this.binanceClient.createWallet().accountStatus(parameters).toString();
    }

    public String getAccountSnapshot() {
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("type", "SPOT");

        return this.binanceClient.createWallet().accountSnapshot(parameters);
    }

    public String getOpenOrders(String symbol) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        return this.binanceClient.createTrade().getOpenOrders(parameters).toString();
    }

    public String cancelOpenOrders(String symbol) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        return this.binanceClient.createTrade().cancelOpenOrders(parameters).toString();
    }

    @Override
    public String cancelAndNewOrderWithStopLoss(String symbol, Double price, Double quantity, Double stopPrice, Double stopLossLimit, Long cancelOrderId) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("side", "SELL");
        parameters.put("type", "STOP_LOSS_LIMIT");
        parameters.put("timeInForce", "GTC");
        parameters.put("quantity", quantity);
        parameters.put("price", price); // Limit price
        parameters.put("stopPrice", stopPrice); // Stop price

        // Optional: For a stop-loss limit order, this is the limit price
        // It's the price at which the stop-loss will be executed and must be set below the stopPrice
        // If you want to set a limit price for stop-loss, you can add this:
        parameters.put("stopLimitPrice", stopLossLimit);
        parameters.put("cancelReplaceMode", "STOP_ON_FAILURE");
        parameters.put("cancelOrderId", cancelOrderId);
        return this.binanceClient.createTrade().cancelReplace(parameters);
    }

    public String newOrder(String symbol, Double price, Double quantity) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("side", "SELL");
        parameters.put("type", "LIMIT");
        parameters.put("timeInForce", "GTC");
        parameters.put("quantity", quantity);
        parameters.put("price", price);

        return this.binanceClient.createTrade().newOrder(parameters);
    }

    public String newOrderWithStopLoss(String symbol, Double price, Double quantity, Double stopPrice, Double stopLossLimit) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("side", "SELL");
        parameters.put("type", "STOP_LOSS_LIMIT");
        parameters.put("timeInForce", "GTC");
        parameters.put("quantity", quantity);
        parameters.put("price", price); // Limit price
        parameters.put("stopPrice", stopPrice); // Stop price

        // Optional: For a stop-loss limit order, this is the limit price
        // It's the price at which the stop-loss will be executed and must be set below the stopPrice
        // If you want to set a limit price for stop-loss, you can add this:
        parameters.put("stopLimitPrice", stopLossLimit);

        return this.binanceClient.createTrade().newOrder(parameters);
    }

    public String getTickerPrice24(String symbol) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("type", "MINI");
        return this.binanceClient.createMarket().ticker24H(parameters);
    }

    public String getCoinPrice(String symbol) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        return this.binanceClient.createMarket().averagePrice(parameters).toString();
    }
}
