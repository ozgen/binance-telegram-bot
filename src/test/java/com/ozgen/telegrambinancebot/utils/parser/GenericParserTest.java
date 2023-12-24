package com.ozgen.telegrambinancebot.utils.parser;


import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class GenericParserTest {
    @Test
    public void testGetDouble_withValidNumber() {
        String validNumber = "123.45";
        Optional<Double> result = GenericParser.getDouble(validNumber);
        assertTrue("Expected result to be present", result.isPresent());
        assertEquals("Parsed value did not match expected output", 123.45, result.get(), 0.0);
    }

    @Test
    public void testGetDouble_withInvalidNumber() {
        String invalidNumber = "abc";
        Optional<Double> result = GenericParser.getDouble(invalidNumber);
        assertFalse("Expected result to be empty for invalid number", result.isPresent());
    }

    @Test
    public void testGetDouble_withNull() {
        String nullString = null;
        Optional<Double> result = GenericParser.getDouble(nullString);
        assertFalse("Expected result to be empty for null input", result.isPresent());
    }
}
