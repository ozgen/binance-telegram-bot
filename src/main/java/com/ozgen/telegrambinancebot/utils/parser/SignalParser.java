package com.ozgen.telegrambinancebot.utils.parser;

import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignalParser {

    private static final String PATTERN = "(\\w+BTC)\\s*ENTRY:\\s*(\\d+\\.\\d+)\\s*-\\s*(\\d+\\.\\d+)((?:\\s*TP\\d+:\\s*\\d+\\.\\d+)+)\\s*STOP:\\s*Close weekly\\s*below\\s*(\\d+\\.\\d+)";
    private static final String TP_PATTERN = "(TP\\d+:\\s*(\\d+\\.\\d+))";

    public static TradingSignal parseSignal(String signalText) {
        TradingSignal tradingSignal = new TradingSignal();

        // Remove empty lines
        String condensedText = signalText.replaceAll("(?m)^[ \t]*\r?\n", "");

        // Main pattern matcher
        Pattern pattern = Pattern.compile(PATTERN, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(condensedText);

        if (matcher.find()) {
            tradingSignal.setSymbol(matcher.group(1));
            tradingSignal.setEntryStart(matcher.group(2));
            tradingSignal.setEntryEnd(matcher.group(3));

            // Parsing take profit values
            List<String> takeProfits = new ArrayList<>();
            Matcher tpMatcher = Pattern.compile(TP_PATTERN).matcher(condensedText);
            while (tpMatcher.find()) {
                takeProfits.add(tpMatcher.group(2));
            }

            tradingSignal.setTakeProfits(takeProfits);
            tradingSignal.setStopLoss(matcher.group(5));
        }

        return tradingSignal;
    }
}

