package com.ozgen.telegrambinancebot.model.events;

import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString
public class IncomingChunkedTradingSignalEvent extends ApplicationEvent {

    private final TradingSignal tradingSignal;
    public IncomingChunkedTradingSignalEvent(Object source, TradingSignal tradingSignal) {
        super(source);
        this.tradingSignal = tradingSignal;
    }

}
