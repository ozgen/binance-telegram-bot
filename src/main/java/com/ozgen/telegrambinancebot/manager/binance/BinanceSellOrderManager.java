package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.bot.SellOrder;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.FutureTradeService;
import com.ozgen.telegrambinancebot.utils.PriceCalculator;
import com.ozgen.telegrambinancebot.utils.SymbolGenerator;
import com.ozgen.telegrambinancebot.utils.parser.GenericParser;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinanceSellOrderManager {

    private static final Logger log = LoggerFactory.getLogger(BinanceSellOrderManager.class);

    private final BinanceApiManager binanceApiManager;
    private final BotConfiguration botConfiguration;
    private final FutureTradeService futureTradeService;
    private final BotOrderService botOrderService;
    private final ApplicationEventPublisher publisher;


    public void processNewSellOrderEvent(NewSellOrderEvent event) {
        BuyOrder buyOrder = event.getBuyOrder();
        SnapshotData accountSnapshot = this.getAccountSnapshot();
        if (accountSnapshot == null) {
            log.error("Failed to get account snapshot for BuyOrder ID {}", buyOrder.getId());
            //todo check this case is handled or not?
            return; // or handle accordingly
        }

        String coinSymbol = SymbolGenerator.getCoinSymbol(buyOrder.getSymbol(), botConfiguration.getCurrency());
        if (coinSymbol == null) {
            log.error("Coin symbol could not be generated for BuyOrder ID {}", buyOrder.getId());
            // todo Handle empty symbol scenario
            return;
        }

        SellOrder sellOrder = this.createSellOrder(buyOrder, accountSnapshot, coinSymbol);
        if (sellOrder != null) {
            log.info("Sell order created successfully for BuyOrder ID {}", buyOrder.getId());
            // Additional logic if required
        } else {
            log.warn("Sell order creation failed for BuyOrder ID {}", buyOrder.getId());
            // Handle failed sell order creation with sell error future trade schedule...
        }
    }

    private SnapshotData getAccountSnapshot() {
        try {
            return binanceApiManager.getAccountSnapshot();
        } catch (Exception e) {
            log.error("Error fetching account snapshot: {}", e.getMessage(), e);
            this.processException(e);
            return null;
        }
    }


    private SellOrder createSellOrder(BuyOrder buyOrder, SnapshotData accountSnapshot, String coinSymbol) {
        Double coinAmount = accountSnapshot.getCoinValue(coinSymbol);
        TradingSignal tradingSignal = buyOrder.getTradingSignal();
        double expectedTotal = buyOrder.getCoinAmount();

        if (!this.isCoinAmountWithinExpectedRange(coinAmount, expectedTotal)) {
            log.error("Coin amount not within expected range for BuyOrder ID {}", buyOrder.getId());
            // Handle or cancel requests as needed
            return null;
        }

        SellOrder sellOrder = this.initializeSellOrder(buyOrder, coinAmount, tradingSignal);
        SellOrder saved = this.executeSellOrder(sellOrder, tradingSignal);
        return saved;
    }

    private boolean isCoinAmountWithinExpectedRange(Double coinAmount, double expectedTotal) {
        if (coinAmount.equals(expectedTotal)) {
            log.info("All buy orders completed successfully.");
            return true;
        } else if (coinAmount > 0 && coinAmount < expectedTotal) {
            log.info("Partial buy orders completed.");
            return true;
        } else {
            return false;
        }
    }

    private SellOrder initializeSellOrder(BuyOrder buyOrder, Double coinAmount, TradingSignal tradingSignal) {
        double sellPrice = PriceCalculator.calculateCoinPriceInc(buyOrder.getBuyPrice(), this.botConfiguration.getProfitPercentage());
        double stopLossLimit = GenericParser.getDouble(tradingSignal.getStopLoss()).get();
        double stopLoss = PriceCalculator.calculateCoinPriceDec(stopLossLimit, this.botConfiguration.getPercentageInc());
        String sellOrderSymbol = SymbolGenerator.generateSellOrderSymbol(buyOrder.getSymbol(), this.botConfiguration.getCurrency());

        SellOrder sellOrder = this.botOrderService.getSellOrder(tradingSignal).orElse(null);
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

        return sellOrder;
    }

    private SellOrder executeSellOrder(SellOrder sellOrder, TradingSignal tradingSignal) {
        try {
            this.binanceApiManager.newOrderWithStopLoss(sellOrder.getSymbol(), sellOrder.getSellPrice(), sellOrder.getCoinAmount(), sellOrder.getStopLoss(), sellOrder.getStopLossLimit());
            log.info("Sell order created successfully for symbol {}", sellOrder.getSymbol());
            return this.botOrderService.createSellOrder(sellOrder);

        } catch (Exception e) {
            log.error("Failed to create sell order for symbol {}: {}", sellOrder.getSymbol(), e.getMessage(), e);
            this.futureTradeService.createFutureTrade(tradingSignal, TradeStatus.ERROR_SELL);
            this.processException(e);
            return null;
        }
    }

    private void processException(Exception e) {
        ErrorEvent errorEvent = new ErrorEvent(this, e);
        this.publisher.publishEvent(errorEvent);
    }
}
