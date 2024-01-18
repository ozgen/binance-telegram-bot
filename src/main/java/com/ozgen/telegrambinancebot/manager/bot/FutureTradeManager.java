package com.ozgen.telegrambinancebot.manager.bot;

import com.ozgen.telegrambinancebot.manager.binance.BinanceApiManager;
import com.ozgen.telegrambinancebot.manager.binance.BinanceTradingSignalManager;
import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.bot.FutureTrade;
import com.ozgen.telegrambinancebot.model.events.NewBuyOrderEvent;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.FutureTradeService;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import com.ozgen.telegrambinancebot.utils.SyncUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FutureTradeManager {
    private static final Logger log = LoggerFactory.getLogger(BinanceTradingSignalManager.class);
    private final BinanceApiManager binanceApiManager;
    private final FutureTradeService futureTradeService;
    private final TradingSignalService tradingSignalService;
    private final BotOrderService botOrderService;
    private final ApplicationEventPublisher publisher;


    public void processFutureTrades(TradeStatus tradeStatus) {
        if (!this.isInsufficientOrNotInRangeOrErrorBuy(tradeStatus)) {
            log.error("trade status should be insufficient, not in range or buy error tradeStatus: '{}'", tradeStatus);
            return;
        }

        List<FutureTrade> futureTrades = this.futureTradeService.getAllFutureTradeByTradeStatus(tradeStatus);
        if (futureTrades.isEmpty()) {
            log.info("There are no {} future trades in the db.", tradeStatus);
            return;
        }

        this.processTrades(futureTrades);
    }

    public void processSellErrorFutureTrades() {
        List<FutureTrade> futureTrades = this.futureTradeService.getAllFutureTradeByTradeStatus(TradeStatus.ERROR_SELL);
        if (futureTrades.isEmpty()) {
            log.info("There are no {} future trades in the db.", TradeStatus.ERROR_SELL);
            return;
        }

        this.processSellTrades(futureTrades);
        this.futureTradeService.deleteFutureTrades(futureTrades);
    }

    private void processTrades(List<FutureTrade> futureTrades) {
        List<String> tradingSignalIdList = futureTrades.stream()
                .map(FutureTrade::getTradeSignalId)
                .distinct()
                .collect(Collectors.toList());

        Map<String, List<FutureTrade>> groupedFutureTrades = this.futureTradeService.getAllFutureTradeByTradingSignals(tradingSignalIdList)
                .stream()
                .collect(Collectors.groupingBy(FutureTrade::getTradeSignalId));

        Map<String, TradingSignal> groupedSignals = this.tradingSignalService.getTradingSignalsByIdList(tradingSignalIdList)
                .stream()
                .collect(Collectors.toMap(TradingSignal::getId, Function.identity(), (existing, replacement) -> existing));

        groupedFutureTrades.forEach((tradeSignalId, tradeList) -> this.processTradeGroup(tradeSignalId, tradeList, groupedSignals));
    }

    private void processSellTrades(List<FutureTrade> futureTrades) {
        List<String> tradingSignalIdList = futureTrades.stream()
                .map(FutureTrade::getTradeSignalId)
                .distinct()
                .collect(Collectors.toList());


        List<TradingSignal> tradingSignals = this.tradingSignalService.getTradingSignalsByIdList(tradingSignalIdList);
        List<BuyOrder> buyOrders = this.botOrderService.getBuyOrders(tradingSignals);
        if (buyOrders.isEmpty()) {
            log.info("There is no buyOrder occured for these sell order error in the db.");
            return;
        }

        buyOrders.forEach(this::publishNewSellOrderEvent);
    }

    private void processTradeGroup(String tradeSignalId, List<FutureTrade> tradeList, Map<String, TradingSignal> groupedSignals) {
        if (tradeList.size() != 1) {
            this.futureTradeService.deleteFutureTrades(tradeList);
            return;
        }

        TradingSignal tradingSignal = groupedSignals.get(tradeSignalId);
        TickerData tickerPrice24 = this.fetchTickerPrice(tradingSignal);
        if (tickerPrice24 != null) {
            this.publishNewBuyOrderEvent(tradingSignal, tickerPrice24);
            this.futureTradeService.deleteFutureTrades(tradeList);
            SyncUtil.pauseBetweenOperations();
        }

    }

    private TickerData fetchTickerPrice(TradingSignal tradingSignal) {
        try {
            return this.binanceApiManager.getTickerPrice24(tradingSignal.getSymbol());
        } catch (Exception e) {
            log.error("Error occurred while executing getTickerPrice24 for symbol: {}", tradingSignal.getSymbol(), e);
            return null;
        }
    }

    private void publishNewBuyOrderEvent(TradingSignal tradingSignal, TickerData tickerPrice24) {
        NewBuyOrderEvent newBuyOrderEvent = new NewBuyOrderEvent(this, tradingSignal, tickerPrice24);
        this.publisher.publishEvent(newBuyOrderEvent);
        SyncUtil.pauseBetweenOperations();
    }

    private void publishNewSellOrderEvent(BuyOrder buyOrder) {
        NewSellOrderEvent newSellOrderEvent = new NewSellOrderEvent(this, buyOrder);
        this.publisher.publishEvent(newSellOrderEvent);
    }

    private boolean isInsufficientOrNotInRangeOrErrorBuy(TradeStatus tradeStatus) {
        return tradeStatus == TradeStatus.INSUFFICIENT || tradeStatus == TradeStatus.NOT_IN_RANGE || tradeStatus == TradeStatus.ERROR_BUY;
    }
}
