package com.ozgen.telegrambinancebot.utils.parser;


import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.utils.TestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignalParserTest {

    @Test
    void testParseSignal() {
        String signalText = TestData.TRADING_SIGNAL;
        TradingSignal signal = SignalParser.parseSignal(signalText);

        assertEquals("NKNBTC", signal.getSymbol());
        assertEquals("0.00000260", signal.getEntryStart());
        assertEquals("0.00000290", signal.getEntryEnd());
        assertEquals(7, signal.getTakeProfits().size());
        assertTrue(signal.getTakeProfits().contains("0.00000315"));
        assertTrue(signal.getTakeProfits().contains("0.00000360"));
        assertTrue(signal.getTakeProfits().contains("0.00000432"));
        assertTrue(signal.getTakeProfits().contains("0.00000486"));
        assertTrue(signal.getTakeProfits().contains("0.00000550"));
        assertTrue(signal.getTakeProfits().contains("0.00000666"));
        assertTrue(signal.getTakeProfits().contains("0.00000741"));
        assertEquals("0.00000240", signal.getStopLoss());
    }

    @Test
    void testParseSignal_2() {
        String signalText = TestData.TRADING_SIGNAL_2;
        TradingSignal signal = SignalParser.parseSignal(signalText);

        assertEquals("ATABTC", signal.getSymbol());
        assertEquals("0.00000240", signal.getEntryStart());
        assertEquals("0.00000280", signal.getEntryEnd());
        assertEquals(7, signal.getTakeProfits().size());
        assertEquals("0.00000220", signal.getStopLoss());
    }

    @Test
    void testParseSignal_3() {
        String signalText = TestData.TRADING_SIGNAL_3;
        TradingSignal signal = SignalParser.parseSignal(signalText);

        assertEquals("MDTBTC", signal.getSymbol());
        assertEquals("0.00000125", signal.getEntryStart());
        assertEquals("0.00000150", signal.getEntryEnd());
        assertEquals(8, signal.getTakeProfits().size());
        assertEquals("0.00000120", signal.getStopLoss());
    }

}
