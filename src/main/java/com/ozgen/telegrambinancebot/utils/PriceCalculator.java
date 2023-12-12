package com.ozgen.telegrambinancebot.utils;

public class PriceCalculator {

    public static double calculateCoinPriceInc(Double buyPrice, double percentage) {
        double increase = buyPrice * (percentage / 100);
        return buyPrice + increase;
    }

    public static double calculateCoinPriceDec(Double buyPrice, double percentage) {
        double increase = buyPrice * (percentage / 100);
        return buyPrice - increase;
    }
}
