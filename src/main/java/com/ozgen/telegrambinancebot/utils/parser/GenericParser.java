package com.ozgen.telegrambinancebot.utils.parser;

import com.ozgen.telegrambinancebot.model.binance.AssetBalance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

public class GenericParser {

    public static Optional<Double> getDouble(String price) {
        try {
            return Optional.ofNullable(price).map(Double::parseDouble);
        } catch (NumberFormatException e) {
            // Log the exception, return empty, or handle it based on your application's needs
            return Optional.empty();
        }
    }

    public static double getFormattedDouble(Double data) {
        if (data == null) {
            return 0; // or throw an IllegalArgumentException
        }

        // Create BigDecimal from String to avoid precision loss
        BigDecimal bd = new BigDecimal(data.toString());

        // Determine the number of decimal places needed
        int decimalPlaces = Math.max(0, -Math.getExponent(data));
        decimalPlaces = Math.min(decimalPlaces, 8); // Set a maximum limit for decimal places

        // Use BigDecimal for precise rounding
        bd = bd.setScale(decimalPlaces, RoundingMode.DOWN);

        // Convert back to double
        return bd.doubleValue();
    }

    public static double getAssetFromSymbol(List<AssetBalance> assets, String symbol) {
        Optional<AssetBalance> targetBalance = assets.stream()
                .filter(assetBalance -> symbol.equals(assetBalance.getAsset()))
                .findFirst();
        if (targetBalance.isPresent()) {
            String freeValue = targetBalance.get().getFree();
            return GenericParser.getDouble(freeValue).get();
        } else {
            return 0.0;
        }
    }
}

