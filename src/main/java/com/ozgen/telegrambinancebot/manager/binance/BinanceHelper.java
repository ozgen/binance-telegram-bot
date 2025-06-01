package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.BotConfiguration;
import com.ozgen.telegrambinancebot.configuration.properties.ScheduleConfiguration;
import com.ozgen.telegrambinancebot.model.binance.AssetBalance;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.utils.DateFactory;
import com.ozgen.telegrambinancebot.utils.PriceCalculator;
import com.ozgen.telegrambinancebot.utils.parser.GenericParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BinanceHelper {

    private final BotConfiguration botConfiguration;
    private final ScheduleConfiguration scheduleConfiguration;
    private final BinanceApiManager binanceApiManager;

    public List<AssetBalance> getUserAssets() {
        try {
            return binanceApiManager.getUserAsset();
        } catch (Exception e) {
            log.error("Error fetching user assets: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public boolean hasAccountEnoughAsset(List<AssetBalance> assets, TradingSignal tradingSignal) throws Exception {
        if (tradingSignal.getInvestAmount() == null) {
            Double btcPriceInUsd = this.getBtcToUsdConversionRate();
            return this.hasAccountEnoughUSD(assets, btcPriceInUsd);
        } else {
            Double investAmountOfBtc = GenericParser.getDouble(tradingSignal.getInvestAmount()).get();
            return this.hasAccountEnoughBTC(assets, investAmountOfBtc);
        }
    }


    public double calculateCoinAmount(double buyPrice, TradingSignal tradingSignal) throws Exception {
        float binanceFeePercentage = this.botConfiguration.getBinanceFeePercentage();
        double investAmountOfBtc = 0d;
        if (tradingSignal.getInvestAmount() == null) {
            double dollarsToInvest = this.botConfiguration.getAmount();
            Double btcPriceInUsd = this.getBtcToUsdConversionRate();
            // Calculate how much BTC you can buy with $500
            investAmountOfBtc = dollarsToInvest / btcPriceInUsd;
        } else {
            investAmountOfBtc = GenericParser.getDouble(tradingSignal.getInvestAmount()).get();
        }
        double binanceFee = investAmountOfBtc * binanceFeePercentage;

        // Calculate how much coin you can buy with that BTC amount
        double coinAmount = (investAmountOfBtc - binanceFee) / buyPrice;

        return coinAmount;
    }

    public boolean isCoinPriceAvailableToSell(Double buyPrice, TickerData tickerData) {
        double sellPrice = PriceCalculator.calculateCoinPriceInc(buyPrice, this.botConfiguration.getProfitPercentage());
        Double lastPrice = GenericParser.getDouble(tickerData.getLastPrice()).get();
        return sellPrice <= lastPrice;
    }

    public Date getSearchDate() {
        return DateFactory.getDateBeforeInMonths(this.scheduleConfiguration.getMonthBefore());
    }

    private Double getBtcToUsdConversionRate() throws Exception {
        TickerData tickerPrice24 = this.binanceApiManager.getTickerPrice24(this.botConfiguration.getCurrencyRate());
        return GenericParser.getDouble(tickerPrice24.getLastPrice()).get();
    }

    private boolean hasAccountEnoughUSD(List<AssetBalance> assets, Double btcToUsdRate) {
        Double btcAmount = GenericParser.getAssetFromSymbol(assets, this.botConfiguration.getCurrency());
        Double totalAccountValueInUsd = btcAmount * btcToUsdRate;
        return totalAccountValueInUsd >= this.botConfiguration.getAmount();
    }

    private boolean hasAccountEnoughBTC(List<AssetBalance> assets, Double btcInvestAmount) {
        Double btcAmount = GenericParser.getAssetFromSymbol(assets, this.botConfiguration.getCurrency());
        return btcAmount >= btcInvestAmount;
    }

}
