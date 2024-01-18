package com.ozgen.telegrambinancebot.adapters.binance.imp;

import com.binance.connector.client.SpotClient;
import com.ozgen.telegrambinancebot.adapters.binance.BinanceAPI;
import com.ozgen.telegrambinancebot.utils.parser.GenericParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DefaultBinanceService implements BinanceAPI {

    private final SpotClient binanceClient;

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
    public String cancelAndNewOrderWithStopLoss(String symbol, Double price, Double quantity, Double stopPrice, Long cancelOrderId) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        parameters.put("symbol", symbol);
        parameters.put("side", "SELL");
        parameters.put("type", "STOP_LOSS_LIMIT");
        parameters.put("timeInForce", "GTC");
        parameters.put("quantity",GenericParser.getFormattedDouble(quantity));
        parameters.put("price", GenericParser.getFormattedDouble(price)); // Limit price
        parameters.put("stopPrice", GenericParser.getFormattedDouble(stopPrice)); // Stop price

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
        parameters.put("quantity", GenericParser.getFormattedDouble(quantity));
        parameters.put("price", GenericParser.getFormattedDouble(price));

        return this.binanceClient.createTrade().newOrder(parameters);
    }

    public String newOrderWithStopLoss(String symbol, Double price, Double quantity, Double stopPrice) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        parameters.put("symbol", symbol);
        parameters.put("side", "SELL");
        parameters.put("stopPrice", GenericParser.getFormattedDouble(stopPrice));
        parameters.put("quantity", GenericParser.getFormattedDouble(quantity));
        parameters.put("price", GenericParser.getFormattedDouble(price));

        return this.binanceClient.createTrade().ocoOrder(parameters);
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
        return this.binanceClient.createMarket().averagePrice(parameters);
    }
}
