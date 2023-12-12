package com.ozgen.telegrambinancebot.utils.parser;


import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignalParserTest {

    @Test
    void testParseSignal() {
        String signalText = "NKNBTC\n\nENTRY: 0.00000260 - 0.00000290\n\nTP1: 0.00000315\nTP2: 0.00000360\nTP3: 0.00000432\nTP4: 0.00000486\nTP5: 0.00000550\nTP6: 0.00000666\nTP7: 0.00000741\n\nSTOP: Close weekly below 0.00000240\n\nSHARED: 18-Nov-2023 @lamatrades ✨\n\nsymbol:NKNBTC\nentry_start: 0.00000260\nentry_end: 0.00000290\nTP1: 0.00000315\nTP2: 0.00000360\nTP3: 0.00000432\nTP4: 0.00000486\nTP5: 0.00000550\nTP6: 0.00000666\nTP7: 0.00000741\nstoploss: 0.00000240";

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

}
