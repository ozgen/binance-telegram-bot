package com.ozgen.telegrambinancebot.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
}
