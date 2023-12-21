package com.ozgen.telegrambinancebot.adapters.binance;

public interface BinanceAPI {

    public String getBTCWalletStatus();

    public String getAccountSnapshot();


    public String getCoinPrice(String symbol);

    public String newOrder(String symbol, Double price, Double quantity);

    public String newOrderWithStopLoss(String symbol, Double price, Double quantity, Double stopPrice, Double stopLossLimit);

    public String getTickerPrice24(String symbol);

    public String getOpenOrders(String symbol);

    public String cancelOpenOrders(String symbol);

    public String cancelAndNewOrderWithStopLoss(String symbol, Double price, Double quantity, Double stopPrice, Double stopLossLimit, Long cancelOrderId);

}
