package com.ozgen.telegrambinancebot.model.events;


import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class NewChunkedBuyOrderEvent extends ApplicationEvent {
    private final TradingSignal tradingSignal;
    private final TickerData tickerData;

    public NewChunkedBuyOrderEvent(Object source, TradingSignal tradingSignal, TickerData tickerData) {
        super(source);
        this.tradingSignal = tradingSignal;
        this.tickerData = tickerData;
    }
}