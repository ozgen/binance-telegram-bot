package com.ozgen.telegrambinancebot.utils.validators;

import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TradingSignalValidatorTest {

    @Test
    void testValidTradingSignal() {
        TradingSignal signal = new TradingSignal();
        signal.setSymbol("BTCUSD");
        signal.setEntryStart("10000");
        signal.setEntryEnd("10500");
        signal.setStopLoss("9500");
        signal.setTakeProfits(Arrays.asList("10600", "10700", "10800"));
        assertTrue(TradingSignalValidator.validate(signal));
    }

    @Test
    void testInvalidTradingSignalWithNull() {
        assertFalse(TradingSignalValidator.validate(null));
    }

    @Test
    void testInvalidTradingSignalWithEmptySymbol() {
        TradingSignal signal = new TradingSignal();
        signal.setEntryStart("10000");
        signal.setEntryEnd("10500");
        signal.setStopLoss("9500");
        signal.setTakeProfits(Arrays.asList("10600", "10700", "10800"));
        assertFalse(TradingSignalValidator.validate(signal));
    }
}
