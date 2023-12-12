package com.ozgen.telegrambinancebot.utils;

public class SymbolGenerator {

    public static final String BTC = "BTC";

    public static String generateBuyOrderSymbol(String symbol, String currency) {
        if (symbol.startsWith(currency)) {
            return symbol;
        } else {
            int btcIndex = symbol.indexOf(currency);
            if (btcIndex != -1) {
                String beforeBTC = symbol.substring(0, btcIndex);
                String afterBTC = symbol.substring(btcIndex + 3);

                return currency + afterBTC + beforeBTC;
            } else {
                return symbol;
            }
        }
    }
    public static String generateSellOrderSymbol(String symbol, String currency) {
        if (symbol.startsWith(currency)) {
            String remaining = symbol.substring(3);
            return remaining + currency;
        } else {
            return symbol;
        }
    }

    public static String getCoinSymbol(String symbol, String currency) {
        if (symbol == null || currency == null) {
            return "INVALID_INPUT"; // or throw an IllegalArgumentException
        }

        // Check if the symbol contains the currency
        if (symbol.contains(currency)) {
            // Remove the currency part from the symbol and return the result
            return symbol.replace(currency, "");
        } else {
            // If the currency is not part of the symbol, return a default or an error
            return null;
        }
    }
}
