package com.ozgen.telegrambinancebot.utils.parser;

import java.util.Optional;

public class GenericParser {

    public static Optional<Double> getDouble(String price) {
        try {
            return Optional.ofNullable(price).map(Double::parseDouble);
        } catch (NumberFormatException e) {
            // Log the exception, return empty, or handle it based on your application's needs
            return Optional.empty();
        }
    }}
