package com.ozgen.telegrambinancebot.utils.parser;

import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class SignalParser {

    private static final String PATTERN = "([A-Z]{6})|ENTRY: ([0-9.]+) - ([0-9.]+)|TP\\d+: ([0-9.]+)|STOP: .* ([0-9.]+)";
    public static TradingSignal parseSignal(String signalText) {
        TradingSignal signal = new TradingSignal();
        Set<String> takeProfits = new HashSet<>();

        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(signalText);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                signal.setSymbol(matcher.group(1));
            } else if (matcher.group(2) != null && matcher.group(3) != null) {
                signal.setEntryStart(matcher.group(2));
                signal.setEntryEnd(matcher.group(3));
            } else if (matcher.group(4) != null) {
                takeProfits.add(matcher.group(4));
            } else if (matcher.group(5) != null) {
                signal.setStopLoss(matcher.group(5));
            }
        }

        signal.setTakeProfits(new ArrayList<>(takeProfits));
        return signal;
    }
}
