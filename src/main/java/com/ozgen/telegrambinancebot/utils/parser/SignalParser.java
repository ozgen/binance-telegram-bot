package com.ozgen.telegrambinancebot.utils.parser;

import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignalParser {

    private static final String PATTERN = "^(\\w+BTC)\\s*\\n\\nENTRY:\\s*(\\d+\\.\\d+)\\s*-\\s*(\\d+\\.\\d+)\\s*\\n\\n(TP\\d+:\\s*\\d+\\.\\d+\\s*)+\\n\\nSTOP:\\s*Close weekly below\\s*(\\d+\\.\\d+)\\s*\\n\\nSHARED:";
    private static final String TP_PATTERN = "(TP\\d+:\\s*(\\d+\\.\\d+))";

    public static TradingSignal parseSignal(String signalText) {
        TradingSignal tradingSignal = new TradingSignal();

        Pattern pattern = Pattern.compile(PATTERN, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(signalText);

        if (matcher.find()) {
            tradingSignal.setSymbol(matcher.group(1));
            tradingSignal.setEntryStart(matcher.group(2));
            tradingSignal.setEntryEnd(matcher.group(3));

            List<String> takeProfits = new ArrayList<>();
            Pattern tpPattern = Pattern.compile(TP_PATTERN);
            Matcher tpMatcher = tpPattern.matcher(signalText);
            while (tpMatcher.find()) {
                takeProfits.add(tpMatcher.group(2));
            }

            tradingSignal.setTakeProfits(takeProfits);
            tradingSignal.setStopLoss(matcher.group(5));
        }

        return tradingSignal;
    }
}

