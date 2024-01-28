package com.ozgen.telegrambinancebot.utils.parser;


import com.ozgen.telegrambinancebot.model.binance.AssetBalance;
import com.ozgen.telegrambinancebot.utils.TestData;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class GenericParserTest {
    @Test
    public void testGetDouble_withValidNumber() {
        String validNumber = "123.45";
        Optional<Double> result = GenericParser.getDouble(validNumber);
        assertTrue( result.isPresent());
        assertEquals(123.45, result.get(), 0.0);
    }

    @Test
    public void testGetDouble_withInvalidNumber() {
        String invalidNumber = "abc";
        Optional<Double> result = GenericParser.getDouble(invalidNumber);
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetDouble_withNull() {
        String nullString = null;
        Optional<Double> result = GenericParser.getDouble(nullString);
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetFormattedDoubleWithNormalValues() {
        assertEquals(123.0, GenericParser.getFormattedDouble(123.4567), "Normal value should be formatted correctly.");
        assertEquals(0.1234, GenericParser.getFormattedDouble(0.123456789), "Value should be rounded to 6 decimal places.");
    }

    @Test
    public void testGetFormattedDoubleWithZero() {
        assertEquals(0.0, GenericParser.getFormattedDouble(0.0), "Zero should be formatted correctly.");
    }

    @Test
    public void testGetFormattedDoubleWithVerySmallValue() {
        assertEquals(0.00000001, GenericParser.getFormattedDouble(0.00000001), "Very small value should be formatted correctly.");
    }

    @Test
    public void testGetFormattedDoubleWithVeryLargeValue() {
        assertEquals(12345678.0, GenericParser.getFormattedDouble(12345678.0), "Very large value should be formatted correctly.");
    }

    @Test
    public void testGetFormattedDoubleWithNull() {
        assertEquals(0.0, GenericParser.getFormattedDouble(null), "Null should return 0.0.");
    }

    @Test
    public void testGetAssetFromSymbol() throws Exception {
        List<AssetBalance> assets = TestData.getAssets();
        assertEquals(0.00004272, GenericParser.getAssetFromSymbol(assets, "BNB"));
    }
}
