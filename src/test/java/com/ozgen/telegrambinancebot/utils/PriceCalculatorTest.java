package com.ozgen.telegrambinancebot.utils;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PriceCalculatorTest {

    @Test
    public void testCalculateCoinPriceInc() {
        double buyPrice = 100.0;
        double percentage = 10.0; // 10% increase
        double expectedPrice = 110.0; // Expected price after 10% increase
        double result = PriceCalculator.calculateCoinPriceInc(buyPrice, percentage);
        assertEquals(expectedPrice, result, 0.001);
    }

    @Test
    public void testCalculateCoinPriceDec() {
        double buyPrice = 100.0;
        double percentage = 10.0; // 10% decrease
        double expectedPrice = 90.0; // Expected price after 10% decrease
        double result = PriceCalculator.calculateCoinPriceDec(buyPrice, percentage);
        assertEquals(expectedPrice, result, 0.001);
    }
}
