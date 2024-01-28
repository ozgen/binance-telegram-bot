package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.model.binance.AssetBalance;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.bot.SellOrder;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.InfoEvent;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.FutureTradeService;
import com.ozgen.telegrambinancebot.utils.PriceCalculator;
import com.ozgen.telegrambinancebot.utils.SymbolGenerator;
import com.ozgen.telegrambinancebot.utils.parser.GenericParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceSellOrderManager {

    private final BinanceApiManager binanceApiManager;
    private final BotConfiguration botConfiguration;
    private final FutureTradeService futureTradeService;
    private final BotOrderService botOrderService;
    private final ApplicationEventPublisher publisher;


    public void processNewSellOrderEvent(NewSellOrderEvent event) {
        BuyOrder buyOrder = event.getBuyOrder();
        List<AssetBalance> assets = this.getUserAssets();
        if (assets == null || assets.isEmpty()) {
            log.error("Failed to get account snapshot for BuyOrder ID {}", buyOrder.getId());
            //todo check this case is handled or not?
            return;
        }

        String coinSymbol = SymbolGenerator.getCoinSymbol(buyOrder.getSymbol(), botConfiguration.getCurrency());
        if (coinSymbol == null) {
            log.error("Coin symbol could not be generated for BuyOrder ID {}", buyOrder.getId());
            // todo Handle empty symbol scenario
            return;
        }

        SellOrder sellOrder = this.createSellOrder(buyOrder, assets, coinSymbol);
        if (sellOrder != null) {
            this.sendSellOrderInfoMessage(sellOrder);
            log.info("Sell order created successfully for BuyOrder ID {}", buyOrder.getId());
            // Additional logic if required
        } else {
            log.warn("Sell order creation failed for BuyOrder ID {}", buyOrder.getId());
            // Handle failed sell order creation with sell error future trade schedule...
        }
    }

    private List<AssetBalance> getUserAssets() {
        try {
            return binanceApiManager.getUserAsset();
        } catch (Exception e) {
            log.error("Error fetching user assets: {}", e.getMessage(), e);
            this.processException(e);
            return null;
        }
    }


    private SellOrder createSellOrder(BuyOrder buyOrder, List<AssetBalance> assets, String coinSymbol) {
        Double coinAmount = GenericParser.getAssetFromSymbol(assets, coinSymbol);
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
        if (coinAmount >= expectedTotal) {
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
        double stopLoss = GenericParser.getDouble(tradingSignal.getStopLoss()).get();
        String sellOrderSymbol = buyOrder.getSymbol();

        SellOrder sellOrder = this.botOrderService.getSellOrder(tradingSignal).orElse(null);
        if (sellOrder == null) {
            sellOrder = new SellOrder();
        }

        sellOrder.setSymbol(sellOrderSymbol);
        sellOrder.setSellPrice(sellPrice);
        sellOrder.setCoinAmount(coinAmount);
        sellOrder.setTimes(sellOrder.getTimes() + 1);
        sellOrder.setStopLoss(stopLoss);
        tradingSignal.setIsProcessed(ProcessStatus.SELL);
        sellOrder.setTradingSignal(tradingSignal);

        return sellOrder;
    }

    private SellOrder executeSellOrder(SellOrder sellOrder, TradingSignal tradingSignal) {
        try {
            this.binanceApiManager.newOrderWithStopLoss(sellOrder.getSymbol(), sellOrder.getSellPrice(), sellOrder.getCoinAmount(), sellOrder.getStopLoss());
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

    private void sendSellOrderInfoMessage(SellOrder sellOrder) {
        InfoEvent infoEvent = new InfoEvent(this, sellOrder.toString());
        this.publisher.publishEvent(infoEvent);
    }
}
