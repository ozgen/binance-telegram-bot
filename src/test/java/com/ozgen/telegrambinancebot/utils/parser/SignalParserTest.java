package com.ozgen.telegrambinancebot.utils.parser;


import com.ozgen.telegrambinancebot.model.TradingStrategy;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.utils.TestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SignalParserTest {

    @Test
    void testParseSignal() {
        String signalText = TestData.TRADING_SIGNAL;
        TradingSignal signal = SignalParser.parseSignal(signalText);

        assertEquals("NKNBTC", signal.getSymbol());
        assertEquals("0.00000260", signal.getEntryStart());
        assertEquals("0.00000290", signal.getEntryEnd());
        assertEquals("0.00000240", signal.getStopLoss());
    }

    @Test
    void testParseSignal_2() {
        String signalText = TestData.TRADING_SIGNAL_2;
        TradingSignal signal = SignalParser.parseSignal(signalText);

        assertEquals("ATABTC", signal.getSymbol());
        assertEquals("0.00000240", signal.getEntryStart());
        assertEquals("0.00000280", signal.getEntryEnd());
        assertEquals("0.00000220", signal.getStopLoss());
    }

    @Test
    void testParseSignal_3() {
        String signalText = TestData.TRADING_SIGNAL_3;
        TradingSignal signal = SignalParser.parseSignal(signalText);

        assertEquals("MDTBTC", signal.getSymbol());
        assertEquals("0.00000125", signal.getEntryStart());
        assertEquals("0.00000150", signal.getEntryEnd());
        assertEquals("0.00000120", signal.getStopLoss());
    }

    @Test
    void testParseSignal_4() {
        String signalText = TestData.TRADING_SIGNAL_4;
        TradingSignal signal = SignalParser.parseSignal(signalText);

        assertEquals("MDTBTC", signal.getSymbol());
        assertEquals("0.00000125", signal.getEntryStart());
        assertEquals("0.00000150", signal.getEntryEnd());
        assertEquals("0.00000120", signal.getStopLoss());
    }

    @Test
    void testParseSignal_SellLater() {
        String signalText = TestData.TRADING_SIGNAL_SELL_LATER;
        TradingSignal signal = SignalParser.parseSignal(signalText);

        assertEquals("MDTBTC", signal.getSymbol());
        assertEquals("0.00000125", signal.getEntryStart());
        assertEquals("0.00000150", signal.getEntryEnd());
        assertEquals("0.00000120", signal.getStopLoss());
        assertEquals("0.0001", signal.getInvestAmount());
        assertEquals(TradingStrategy.SELL_LATER, signal.getStrategy());
    }

}
