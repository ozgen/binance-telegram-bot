package com.ozgen.telegrambinancebot.model.events;

import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import org.springframework.context.ApplicationEvent;

public class NewBuyOrderEvent extends ApplicationEvent {
    private final TradingSignal tradingSignal;
    private final TickerData tickerData;

    public NewBuyOrderEvent(Object source, TradingSignal tradingSignal, TickerData tickerData) {
        super(source);
        this.tradingSignal = tradingSignal;
        this.tickerData = tickerData;
    }


    public TradingSignal getTradingSignal() {
        return tradingSignal;
    }

    public TickerData getTickerData() {
        return tickerData;
    }

    @Override
    public String toString() {
        return "NewBuyOrderEvent{" +
                "tradingSignal=" + tradingSignal +
                ", tickerData=" + tickerData +
                '}';
    }
}
