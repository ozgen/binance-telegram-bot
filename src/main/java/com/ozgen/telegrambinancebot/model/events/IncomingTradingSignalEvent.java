package com.ozgen.telegrambinancebot.model.events;

import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import org.springframework.context.ApplicationEvent;

public class IncomingTradingSignalEvent extends ApplicationEvent {

    private final TradingSignal tradingSignal;
    public IncomingTradingSignalEvent(Object source, TradingSignal tradingSignal) {
        super(source);
        this.tradingSignal = tradingSignal;
    }

    public TradingSignal getTradingSignal() {
        return tradingSignal;
    }

    @Override
    public String toString() {
        return "IncomingTradingSignalEvent{" +
                "tradingSignal=" + tradingSignal +
                '}';
    }
}
