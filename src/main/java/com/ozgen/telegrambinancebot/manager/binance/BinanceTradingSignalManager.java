package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.events.IncomingTradingSignalEvent;
import com.ozgen.telegrambinancebot.model.events.NewBuyOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.utils.validators.TradingSignalValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinanceTradingSignalManager {

    private static final Logger log = LoggerFactory.getLogger(BinanceTradingSignalManager.class);

    private final BinanceApiManager binanceApiManager;
    private final ApplicationEventPublisher publisher;


    public void processIncomingTradingSignalEvent(IncomingTradingSignalEvent event) {

        TradingSignal tradingSignal = event.getTradingSignal();
        String symbol = tradingSignal.getSymbol();
        TickerData tickerPrice24 = null;
        try {
            tickerPrice24 = this.binanceApiManager.getTickerPrice24(symbol);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        boolean availableToBuy = TradingSignalValidator.isAvailableToBuy(tickerPrice24, tradingSignal);
        if (availableToBuy) {
            NewBuyOrderEvent newBuyOrderEvent = new NewBuyOrderEvent(this, tradingSignal, tickerPrice24);
            this.publisher.publishEvent(newBuyOrderEvent);
        }

    }

}
