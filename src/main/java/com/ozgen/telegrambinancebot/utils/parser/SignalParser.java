package com.ozgen.telegrambinancebot.utils.parser;

import com.ozgen.telegrambinancebot.model.TradingStrategy;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SignalParser {


    public static TradingSignal parseSignal(String signalText) {
        TradingSignal tradingSignal = new TradingSignal();
        List<String> lines = Arrays.stream(signalText.split("\\r?\\n"))
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.toList());

        // Extracting the symbol
        tradingSignal.setSymbol(lines.get(0).trim());

        String[] entryPoints = lines.get(1).substring(lines.get(1).indexOf(':') + 1).trim().split(" - ");
        if (entryPoints.length == 2) {
            tradingSignal.setEntryStart(entryPoints[0].trim());
            tradingSignal.setEntryEnd(entryPoints[1].trim());
        }

        List<String> takeProfits = new ArrayList<>();
        for (int i = 2; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("TP")) {
                String tpValue = line.substring(line.indexOf(':') + 1).trim();
                takeProfits.add(tpValue);
            } else if (line.startsWith("STOP")) {
                String[] stopParts = line.split("\\s+");
                String stopLossValue = stopParts[stopParts.length - 1].trim();
                tradingSignal.setStopLoss(stopLossValue);
                break;
            }
            if (line.startsWith("INVEST")) {
                String[] investParts = line.split("\\s+");
                String investValue = investParts[investParts.length - 1].trim();
                tradingSignal.setInvestAmount(investValue);
            }
            if (line.startsWith("STRATEGY")) {
                String[] strategyParts = line.split("\\s+");
                String investValue = strategyParts[strategyParts.length - 1].trim();
                if (investValue.contains("SELL_LATER")) {
                    tradingSignal.setStrategy(TradingStrategy.SELL_LATER);
                }
            }
            tradingSignal.setTakeProfits(takeProfits);
        }

        return tradingSignal;
    }
}

