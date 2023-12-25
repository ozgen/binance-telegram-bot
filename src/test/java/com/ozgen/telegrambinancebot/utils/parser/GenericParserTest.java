package com.ozgen.telegrambinancebot.utils.parser;


import org.junit.jupiter.api.Test;

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
}
