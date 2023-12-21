package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.bot.SellOrder;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.FutureTradeService;
import com.ozgen.telegrambinancebot.utils.PriceCalculator;
import com.ozgen.telegrambinancebot.utils.SymbolGenerator;
import com.ozgen.telegrambinancebot.utils.parser.GenericParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

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
        SnapshotData accountSnapshot = getAccountSnapshot();
        if (accountSnapshot == null) {
            log.error("Failed to get account snapshot for BuyOrder ID {}", buyOrder.getId());
            return; // or handle accordingly
        }

        String coinSymbol = SymbolGenerator.getCoinSymbol(buyOrder.getSymbol(), botConfiguration.getCurrency());
        if (coinSymbol == null) {
            log.error("Coin symbol could not be generated for BuyOrder ID {}", buyOrder.getId());
            // todo Handle empty symbol scenario
            return;
        }

        SellOrder sellOrder = createSellOrder(buyOrder, accountSnapshot, coinSymbol);
        if (sellOrder != null) {
            log.info("Sell order created successfully for BuyOrder ID {}", buyOrder.getId());
            // Additional logic if required
        } else {
            log.warn("Sell order creation failed for BuyOrder ID {}", buyOrder.getId());
            // Handle failed sell order creation
        }
    }

    private SnapshotData getAccountSnapshot() {
        try {
            return binanceApiManager.getAccountSnapshot();
        } catch (Exception e) {
            log.error("Error fetching account snapshot: {}", e.getMessage(), e);
            return null;
        }
    }


    private SellOrder createSellOrder(BuyOrder buyOrder, SnapshotData accountSnapshot, String coinSymbol) {
        Double coinAmount = accountSnapshot.getCoinValue(coinSymbol);
        TradingSignal tradingSignal = buyOrder.getTradingSignal();
        double expectedTotal = buyOrder.getCoinAmount() * buyOrder.getTimes();

        if (!isCoinAmountWithinExpectedRange(coinAmount, buyOrder.getCoinAmount(), expectedTotal)) {
            log.error("Coin amount not within expected range for BuyOrder ID {}", buyOrder.getId());
            // Handle or cancel requests as needed
            return null;
        }

        SellOrder sellOrder = initializeSellOrder(buyOrder, coinAmount, tradingSignal);
        executeSellOrder(sellOrder, tradingSignal);
        return sellOrder;
    }

    private boolean isCoinAmountWithinExpectedRange(Double coinAmount, Double buyOrderAmount, double expectedTotal) {
        if (coinAmount.equals(expectedTotal)) {
            log.info("All buy orders completed successfully.");
            return true;
        } else if (coinAmount > buyOrderAmount && coinAmount < expectedTotal) {
            log.info("Partial buy orders completed.");
            return true;
        } else {
            return false;
        }
    }

    private SellOrder initializeSellOrder(BuyOrder buyOrder, Double coinAmount, TradingSignal tradingSignal) {
        double sellPrice = PriceCalculator.calculateCoinPriceInc(buyOrder.getBuyPrice(), botConfiguration.getProfitPercentage());
        double stopLossLimit = PriceCalculator.calculateCoinPriceDec(buyOrder.getBuyPrice(), botConfiguration.getPercentageInc());
        double stopLoss = GenericParser.getDouble(tradingSignal.getStopLoss());
        String sellOrderSymbol = SymbolGenerator.generateSellOrderSymbol(buyOrder.getSymbol(), botConfiguration.getCurrency());

        SellOrder sellOrder = botOrderService.getSellOrder(tradingSignal).orElse(null);
        if (sellOrder == null) {
            sellOrder = new SellOrder();
        }

        sellOrder.setSymbol(sellOrderSymbol);
        sellOrder.setSellPrice(sellPrice);
        sellOrder.setCoinAmount(coinAmount);
        sellOrder.setTimes(sellOrder.getTimes() + 1);
        sellOrder.setStopLoss(stopLoss);
        sellOrder.setStopLossLimit(stopLossLimit);
        tradingSignal.setIsProcessed(ProcessStatus.SELL);
        sellOrder.setTradingSignal(tradingSignal);
        sellOrder = botOrderService.createSellOrder(sellOrder);

        return sellOrder;
    }

    private void executeSellOrder(SellOrder sellOrder, TradingSignal tradingSignal) {
        try {
            binanceApiManager.newOrderWithStopLoss(sellOrder.getSymbol(), sellOrder.getSellPrice(), sellOrder.getCoinAmount(), sellOrder.getStopLoss(), sellOrder.getStopLossLimit());
            log.info("Sell order created successfully for symbol {}", sellOrder.getSymbol());
        } catch (Exception e) {
            log.error("Failed to create sell order for symbol {}: {}", sellOrder.getSymbol(), e.getMessage(), e);
            futureTradeService.createFutureTrade(tradingSignal, TradeStatus.ERROR_SELL);
        }
    }
}
