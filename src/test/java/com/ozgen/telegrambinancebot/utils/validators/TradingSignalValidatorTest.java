package com.ozgen.telegrambinancebot.utils.validators;

import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TradingSignalValidatorTest {

    @Test
    public void testValidTradingSignal_withValidSignal() {
        TradingSignal signal = new TradingSignal();
        signal.setSymbol("BTCUSD");
        signal.setEntryStart("10000");
        signal.setEntryEnd("10500");
        signal.setStopLoss("9500");
        assertTrue(TradingSignalValidator.validate(signal));
    }

    @Test
    public void testInvalidTradingSignal_withNull() {
        assertFalse(TradingSignalValidator.validate(null));
    }

    @Test
    public void testInvalidTradingSignal_withEmptySymbol() {
        TradingSignal signal = new TradingSignal();
        signal.setEntryStart("10000");
        signal.setEntryEnd("10500");
        signal.setStopLoss("9500");
        assertFalse(TradingSignalValidator.validate(signal));
    }

    @Test
    public void testIsAvailableToBuy_withinRange() {
        TickerData tickerData = new TickerData();
        tickerData.setLastPrice("100.0");
        TradingSignal tradingSignal = new TradingSignal();
        tradingSignal.setEntryStart("95.0");
        tradingSignal.setEntryEnd("105.0");
        assertTrue(TradingSignalValidator.isAvailableToBuy(tickerData, tradingSignal));
    }

    @Test
    public void testIsAvailableToBuy_withAboveRange() {
        TickerData tickerData = new TickerData();
        tickerData.setLastPrice("110.0");
        TradingSignal tradingSignal = new TradingSignal();
        tradingSignal.setEntryStart("95.0");
        tradingSignal.setEntryEnd("105.0");
        assertFalse(TradingSignalValidator.isAvailableToBuy(tickerData, tradingSignal));
    }

    @Test
    public void testIsAvailableToBuy_withBelowRange() {
        TickerData tickerData = new TickerData();
        tickerData.setLastPrice("90.0");
        TradingSignal tradingSignal = new TradingSignal();
        tradingSignal.setEntryStart("95.0");
        tradingSignal.setEntryEnd("105.0");
        assertFalse(TradingSignalValidator.isAvailableToBuy(tickerData, tradingSignal));
    }

    @Test
    public void testIsAvailableToBuy_withAtRangeStart() {
        TickerData tickerData = new TickerData();
        tickerData.setLastPrice("95.0");
        TradingSignal tradingSignal = new TradingSignal();
        tradingSignal.setEntryStart("95.0");
        tradingSignal.setEntryEnd("105.0");
        assertTrue(TradingSignalValidator.isAvailableToBuy(tickerData, tradingSignal));
    }

    @Test
    public void testIsAvailableToBuy_withAtRangeEnd() {
        TickerData tickerData = new TickerData();
        tickerData.setLastPrice("105.0");
        TradingSignal tradingSignal = new TradingSignal();
        tradingSignal.setEntryStart("95.0");
        tradingSignal.setEntryEnd("105.0");
        assertTrue(TradingSignalValidator.isAvailableToBuy(tickerData, tradingSignal));
    }

    @Test
    public void testIsAvailableToBuyWithinRange() {
        double buyPrice = 100.0;
        TradingSignal tradingSignal = new TradingSignal();
        tradingSignal.setEntryStart("95.0");
        tradingSignal.setEntryEnd("105.0");
        assertTrue(TradingSignalValidator.isAvailableToBuy(buyPrice, tradingSignal));
    }

    @Test
    public void testIsAvailableToBuyAboveRange() {
        double buyPrice = 250.0;
        TradingSignal tradingSignal = new TradingSignal();
        tradingSignal.setEntryStart("95.0");
        tradingSignal.setEntryEnd("105.0");
        assertFalse(TradingSignalValidator.isAvailableToBuy(buyPrice, tradingSignal));
    }

    @Test
    public void testIsAvailableToBuyBelowRange() {
        double buyPrice = 50.0;
        TradingSignal tradingSignal = new TradingSignal();
        tradingSignal.setEntryStart("95.0");
        tradingSignal.setEntryEnd("105.0");
        assertFalse(TradingSignalValidator.isAvailableToBuy(buyPrice, tradingSignal));
    }

    @Test
    public void testIsAvailableToBuyAtRangeStart() {
        double buyPrice = 95.0;
        TradingSignal tradingSignal = new TradingSignal();
        tradingSignal.setEntryStart("95.0");
        tradingSignal.setEntryEnd("105.0");
        assertTrue(TradingSignalValidator.isAvailableToBuy(buyPrice, tradingSignal));
    }

    @Test
    public void testIsAvailableToBuyAtRangeEnd() {
        double buyPrice = 105.0;
        TradingSignal tradingSignal = new TradingSignal();
        tradingSignal.setEntryStart("95.0");
        tradingSignal.setEntryEnd("105.0");
        assertTrue(TradingSignalValidator.isAvailableToBuy(buyPrice, tradingSignal));
    }

}
