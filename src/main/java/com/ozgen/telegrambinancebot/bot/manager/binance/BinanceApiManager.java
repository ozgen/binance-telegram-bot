package com.ozgen.telegrambinancebot.bot.manager.binance;

import com.ozgen.telegrambinancebot.adapters.binance.BinanceAPI;
import com.ozgen.telegrambinancebot.bot.service.AccountSnapshotService;
import com.ozgen.telegrambinancebot.bot.service.BinanceOrderService;
import com.ozgen.telegrambinancebot.bot.service.TickerDataService;
import com.ozgen.telegrambinancebot.model.binance.OpenOrder;
import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
import com.ozgen.telegrambinancebot.model.binance.OrderResponse;
import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.utils.parser.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BinanceApiManager {
    private static final Logger log = LoggerFactory.getLogger(BinanceApiManager.class);

    private final BinanceAPI binanceAPI;
    private final TickerDataService tickerDataService;
    private final BinanceOrderService binanceOrderService;
    private final AccountSnapshotService accountSnapshotService;

    public BinanceApiManager(BinanceAPI binanceAPI, TickerDataService tickerDataService, BinanceOrderService binanceOrderService,
                             AccountSnapshotService accountSnapshotService) {
        this.binanceAPI = binanceAPI;
        this.tickerDataService = tickerDataService;
        this.binanceOrderService = binanceOrderService;
        this.accountSnapshotService = accountSnapshotService;
    }




    TickerData getTickerPrice24(String symbol) throws Exception {
        String tickerPrice24 = this.binanceAPI.getTickerPrice24(symbol);
        TickerData tickerData = JsonParser.parseTickerJson(tickerPrice24);

        log.info("'{}' of symbol ticker data is parsed, successfully.", symbol);
        return this.tickerDataService.createTickerData(tickerData);
    }

    SnapshotData getAccountSnapshot() throws Exception {
        String accountSnapshotJson = this.binanceAPI.getAccountSnapshot();
        SnapshotData snapshotData = JsonParser.parseSnapshotJson(accountSnapshotJson);

        log.info("account snapshot data is parsed, successfully.");
        return this.accountSnapshotService.createSnapshotData(snapshotData);
    }

    List<OrderInfo> getOpenOrders(String symbol) throws Exception {
        String openOrdersJson = this.binanceAPI.getOpenOrders(symbol);
        List<OrderInfo> orderInfoList = JsonParser.parseOrderInfoJson(openOrdersJson);

        log.info("'{}' of symbol open orders data are parsed, successfully.", symbol);
        return this.binanceOrderService.createOrderInfos(orderInfoList);
    }

    List<OpenOrder> cancelOpenOrders(String symbol) throws Exception {
        String openOrdersJson = this.binanceAPI.cancelOpenOrders(symbol);
        List<OpenOrder> openOrderList = JsonParser.parseOpenOrdersJson(openOrdersJson);

        log.info("'{}' of symbol cancel open orders data are parsed, successfully.", symbol);
        return this.binanceOrderService.createOpenOrders(openOrderList);
    }

    OrderResponse newOrder(String symbol, Double price, Double quantity) throws Exception {
        String orderResponseJson = this.binanceAPI.newOrder(symbol, price, quantity);
        OrderResponse orderResponse = JsonParser.parseOrderResponseJson(orderResponseJson);

        log.info("'{}' of symbol order response data are parsed, successfully.", symbol);
        return this.binanceOrderService.createOrderResponse(orderResponse);
    }

    OrderResponse newOrderWithStopLoss(String symbol, Double price, Double quantity, Double stopPrice, Double stopLossLimit) throws Exception {
        String orderResponseJson = this.binanceAPI.newOrderWithStopLoss(symbol, price, quantity, stopPrice, stopLossLimit);
        OrderResponse orderResponse = JsonParser.parseOrderResponseJson(orderResponseJson);

        log.info("'{}' of symbol order response data are parsed, successfully.", symbol);
        return this.binanceOrderService.createOrderResponse(orderResponse);
    }

}
