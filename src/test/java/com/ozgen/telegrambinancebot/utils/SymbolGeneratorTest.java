package com.ozgen.telegrambinancebot.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class SymbolGeneratorTest {

    @Test
    void generateBuyOrderSymbol() {
        assertEquals("BTCNKN", SymbolGenerator.generateBuyOrderSymbol("NKNBTC", "BTC"));
        assertEquals("BTCXYZ", SymbolGenerator.generateBuyOrderSymbol("BTCXYZ", "BTC"));
        assertEquals("BTCABCD", SymbolGenerator.generateBuyOrderSymbol("BTCABCD", "BTC"));
        assertEquals("BTCETH", SymbolGenerator.generateBuyOrderSymbol("ETHBTC", "BTC"));
    }

    @Test
    void generateSellOrderSymbol() {
        assertEquals("NKNBTC", SymbolGenerator.generateSellOrderSymbol("BTCNKN", "BTC"));
        assertEquals("XYZBTC", SymbolGenerator.generateSellOrderSymbol("BTCXYZ", "BTC"));
        assertEquals("ABCDBTC", SymbolGenerator.generateSellOrderSymbol("BTCABCD", "BTC"));
        assertEquals("ETHBTC", SymbolGenerator.generateSellOrderSymbol("ETHBTC", "BTC"));
    }

    @Test
    public void testGetCoinSymbolWithCurrencyInSymbol() {
        String symbol = "BTCUSD";
        String currency = "USD";
        String expected = "BTC";
        assertEquals(expected, SymbolGenerator.getCoinSymbol(symbol, currency));
    }

    @Test
    public void testGetCoinSymbolWithCurrencyNotInSymbol() {
        String symbol = "BTC";
        String currency = "USD";
        assertNull(SymbolGenerator.getCoinSymbol(symbol, currency));
    }

    @Test
    public void testGetCoinSymbolWithNullSymbol() {
        String symbol = null;
        String currency = "USD";
        assertNull(SymbolGenerator.getCoinSymbol(symbol, currency));
    }

    @Test
    public void testGetCoinSymbolWithNullCurrency() {
        String symbol = "BTCUSD";
        String currency = null;
        assertNull(SymbolGenerator.getCoinSymbol(symbol, currency));
    }

    @Test
    public void testGetCoinSymbolWithBothNull() {
        String symbol = null;
        String currency = null;
        assertNull(SymbolGenerator.getCoinSymbol(symbol, currency));
    }

}
