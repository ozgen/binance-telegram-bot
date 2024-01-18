package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.adapters.binance.BinanceAPI;
import com.ozgen.telegrambinancebot.model.binance.CancelAndNewOrderResponse;
import com.ozgen.telegrambinancebot.service.AccountSnapshotService;
import com.ozgen.telegrambinancebot.service.BinanceOrderService;
import com.ozgen.telegrambinancebot.service.TickerDataService;
import com.ozgen.telegrambinancebot.model.binance.OpenOrder;
import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
import com.ozgen.telegrambinancebot.model.binance.OrderResponse;
import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.utils.parser.JsonParser;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BinanceApiManager {
    private static final Logger log = LoggerFactory.getLogger(BinanceApiManager.class);

    private final BinanceAPI binanceAPI;
    private final TickerDataService tickerDataService;
    private final BinanceOrderService binanceOrderService;
    private final AccountSnapshotService accountSnapshotService;


    public TickerData getTickerPrice24(String symbol) throws Exception {
        String tickerPrice24 = this.binanceAPI.getTickerPrice24(symbol);
        TickerData tickerData = JsonParser.parseTickerJson(tickerPrice24);

        log.info("'{}' of symbol ticker data is parsed, successfully.", symbol);
        return this.tickerDataService.createTickerData(tickerData);
    }

    SnapshotData getAccountSnapshot() throws Exception {
        String accountSnapshotJson = this.binanceAPI.getAccountSnapshot();
        SnapshotData snapshotData = JsonParser.parseSnapshotJson(accountSnapshotJson);

        log.info("account snapshot data is parsed, successfully.");
        // todo due to thıs process takes too long, commented the saving DB operatıon
//        return this.accountSnapshotService.createSnapshotData(snapshotData);
        return snapshotData;
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

    OrderResponse newOrderWithStopLoss(String symbol, Double price, Double quantity, Double stopPrice) throws Exception {
        String orderResponseJson = this.binanceAPI.newOrderWithStopLoss(symbol, price, quantity, stopPrice);
        OrderResponse orderResponse = JsonParser.parseOrderResponseJson(orderResponseJson);

        log.info("'{}' of symbol order response data are parsed, successfully.", symbol);
        return this.binanceOrderService.createOrderResponse(orderResponse);
    }

    CancelAndNewOrderResponse cancelAndNewOrderWithStopLoss(String symbol, Double price, Double quantity, Double stopPrice, Long cancelOrderId) throws Exception {
        String orderResponseJson = this.binanceAPI.cancelAndNewOrderWithStopLoss(symbol, price, quantity, stopPrice, cancelOrderId);
        CancelAndNewOrderResponse orderResponse = JsonParser.parseCancelAndNewOrderResponseJson(orderResponseJson);

        log.info("'{}' of symbol cancel and new order response data are parsed, successfully.", symbol);
        return this.binanceOrderService.createCancelAndNewOrderResponse(orderResponse);
    }

}
