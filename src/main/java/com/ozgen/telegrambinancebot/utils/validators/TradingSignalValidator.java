package com.ozgen.telegrambinancebot.utils.validators;

import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.utils.parser.GenericParser;

public class TradingSignalValidator {

    public static boolean validate(TradingSignal tradingSignal) {
        if (tradingSignal == null) {
            return false;
        }
        if (tradingSignal.getSymbol() == null || tradingSignal.getSymbol().isEmpty()) {
            return false;
        }
        if (tradingSignal.getEntryStart() == null || tradingSignal.getEntryStart().isEmpty()) {
            return false;
        }
        if (tradingSignal.getEntryEnd() == null || tradingSignal.getEntryEnd().isEmpty()) {
            return false;
        }
        if (tradingSignal.getStopLoss() == null || tradingSignal.getStopLoss().isEmpty()) {
            return false;
        }
        if (tradingSignal.getTakeProfits().isEmpty()) {
            return false;
        }
        return true;
    }

    public static boolean isAvailableToBuy(TickerData tickerData, TradingSignal tradingSignal) {
        Double entryStart = GenericParser.getDouble(tradingSignal.getEntryStart()).get();
        Double entryEnd = GenericParser.getDouble(tradingSignal.getEntryEnd()).get();
        Double lastPrice = GenericParser.getDouble(tickerData.getLastPrice()).get();
        return lastPrice >= entryStart && lastPrice <= entryEnd;
    }

    public static boolean isAvailableToBuy(double buyPrice, TradingSignal tradingSignal) {
        Double entryStart = GenericParser.getDouble(tradingSignal.getEntryStart()).get();
        Double entryEnd = GenericParser.getDouble(tradingSignal.getEntryEnd()).get();
        return buyPrice >= entryStart && buyPrice <= entryEnd;
    }
}
