package com.ozgen.telegrambinancebot.bot.manager.binance;

import com.ozgen.telegrambinancebot.bot.service.BotOrderService;
import com.ozgen.telegrambinancebot.bot.service.FutureTradeService;
import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.bot.FutureTrade;
import com.ozgen.telegrambinancebot.model.bot.SellOrder;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.utils.PriceCalculator;
import com.ozgen.telegrambinancebot.utils.SymbolGenerator;
import com.ozgen.telegrambinancebot.utils.parser.GenericParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BinanceSellOrderManager {

    private static final Logger log = LoggerFactory.getLogger(BinanceSellOrderManager.class);

    private final BinanceApiManager binanceApiManager;
    private final ApplicationEventPublisher publisher;
    private final BotConfiguration botConfiguration;
    private final FutureTradeService futureTradeService;

    private final BotOrderService botOrderService;


    public BinanceSellOrderManager(BinanceApiManager binanceApiManager, ApplicationEventPublisher publisher, BotConfiguration botConfiguration, FutureTradeService futureTradeService, BotOrderService botOrderService) {
        this.binanceApiManager = binanceApiManager;
        this.publisher = publisher;
        this.botConfiguration = botConfiguration;
        this.futureTradeService = futureTradeService;
        this.botOrderService = botOrderService;

    }

    public void processNewSellOrderEvent(NewSellOrderEvent event) {
        BuyOrder buyOrder = event.getBuyOrder();
        SnapshotData accountSnapshot = null;
        try {
            accountSnapshot = this.binanceApiManager.getAccountSnapshot();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String coinSymbol = SymbolGenerator.getCoinSymbol(buyOrder.getSymbol(), this.botConfiguration.getCurrency());
        if (coinSymbol == null) {
            //todo handle empty symbol
            return;
        }

        SellOrder sellOrder = this.createSellOrder(buyOrder, accountSnapshot, coinSymbol);

        if (sellOrder != null) {

            log.info("NewSellOrderEvent published successfully. NewSellOrderEvent: {}", "null");
        }
    }

    private List<Double> calculateCoinAmount(double actualCoinAmount, double buyOrderCoinAmount) {
        List<Double> coinAmounts = new ArrayList<>();
        while (actualCoinAmount > buyOrderCoinAmount) {
            coinAmounts.add(buyOrderCoinAmount);
            actualCoinAmount -= buyOrderCoinAmount;
        }

        if (actualCoinAmount > 0) {
            coinAmounts.add(actualCoinAmount);
        }

        return coinAmounts;
    }

    private SellOrder createSellOrder(BuyOrder buyOrder, SnapshotData accountSnapshot, String coinSymbol) {
        Double coinAmount = accountSnapshot.getCoinValue(coinSymbol);
        TradingSignal tradingSignal = buyOrder.getTradingSignal();
        double expectedTotal = buyOrder.getCoinAmount() * buyOrder.getTimes();
        if (coinAmount == expectedTotal) {
            log.info("all buy orders completed. successfully.");
        } else if (coinAmount > buyOrder.getCoinAmount() && coinAmount < expectedTotal) {
            log.info("all buy orders not completed. successfully.");
        } else {
            log.error("No buy orders completed. successfully.");
            //todo handle or cancel requests
            return null;
        }

        List<Double> coinAmountList = this.calculateCoinAmount(coinAmount, buyOrder.getCoinAmount());
        double sellPrice = PriceCalculator.calculateCoinPriceInc(buyOrder.getBuyPrice(), this.botConfiguration.getProfitPercentage());
        double stopLoss = GenericParser.getDouble(tradingSignal.getStopLoss());
        double stopLossLimit = PriceCalculator.calculateCoinPriceDec(buyOrder.getBuyPrice(), this.botConfiguration.getPercentageInc());
        String sellOrderSymbol = SymbolGenerator.generateSellOrderSymbol(buyOrder.getSymbol(), this.botConfiguration.getCurrency());
        SellOrder sellOrder = new SellOrder();
        sellOrder.setSymbol(sellOrderSymbol);
        sellOrder.setSellPrice(sellPrice);
        sellOrder.setCoinAmount(buyOrder.getCoinAmount());
        sellOrder.setTimes(coinAmountList.size());
        sellOrder.setStopLoss(stopLoss);
        sellOrder.setStopLossLimit(stopLossLimit);
        sellOrder.setTradingSignal(tradingSignal);
        sellOrder = this.botOrderService.createSellOrder(sellOrder);
        coinAmountList.forEach(amount -> {
            try {
                this.binanceApiManager.newOrderWithStopLoss(sellOrderSymbol, sellPrice, amount, stopLoss, stopLossLimit);
            } catch (Exception e) {
                log.error("Failed to create sell order  for symbol " + sellOrderSymbol, e);
                FutureTrade futureTrade = new FutureTrade();
                futureTrade.setTradeStatus(TradeStatus.ERROR_SELL);
                futureTrade.setTradeSignalId(buyOrder.getTradingSignal().getId());
                this.futureTradeService.createFutureTrade(futureTrade);
                // todo handle future trades
            }
        });

        return sellOrder;
    }

}
