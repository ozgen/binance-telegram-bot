package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.IncomingChunkedTradingSignalEvent;
import com.ozgen.telegrambinancebot.model.events.IncomingTradingSignalEvent;
import com.ozgen.telegrambinancebot.model.events.NewBuyOrderEvent;
import com.ozgen.telegrambinancebot.model.events.NewChunkedBuyExecutionEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.utils.validators.TradingSignalValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceTradingSignalManager {

    private final BinanceApiManager binanceApiManager;
    private final ApplicationEventPublisher publisher;


    public void processIncomingTradingSignalEvent(IncomingTradingSignalEvent event) {
        TradingSignal tradingSignal = event.getTradingSignal();
        String symbol = tradingSignal.getSymbol();

        log.info("Processing incoming trading signal event for symbol: {}", symbol);

        TickerData tickerPrice24;
        try {
            tickerPrice24 = this.binanceApiManager.getTickerPrice24(symbol);
            log.info("Fetched ticker price for symbol {}: {}", symbol, tickerPrice24);
        } catch (Exception e) {
            log.error("Error occurred while fetching ticker price for symbol {}: {}", symbol, e.getMessage(), e);
            this.processException(e);
            throw new RuntimeException("Error fetching ticker price", e);
        }

        boolean availableToBuy = TradingSignalValidator.isAvailableToBuy(tickerPrice24, tradingSignal);
        log.info("Availability to buy for symbol {}: {}", symbol, availableToBuy);

        if (availableToBuy) {
            NewBuyOrderEvent newBuyOrderEvent = new NewBuyOrderEvent(this, tradingSignal, tickerPrice24);
            log.info("Publishing NewBuyOrderEvent for symbol {}", symbol);
            this.publisher.publishEvent(newBuyOrderEvent);
        } else {
            log.info("Symbol {} is not available to buy", symbol);
        }
    }

    public void processIncomingChunkedTradingSignalEvent(IncomingChunkedTradingSignalEvent event) {
        TradingSignal tradingSignal = event.getTradingSignal();
        String symbol = tradingSignal.getSymbol();

        log.info("Processing incoming CHUNKED trading signal for symbol: {}", symbol);

        TickerData tickerPrice24;
        try {
            tickerPrice24 = this.binanceApiManager.getTickerPrice24(symbol);
            log.info("Fetched ticker price for symbol {}: {}", symbol, tickerPrice24);
        } catch (Exception e) {
            log.error("Error occurred while fetching ticker price for symbol {}: {}", symbol, e.getMessage(), e);
            this.processException(e);
            throw new RuntimeException("Error fetching ticker price", e);
        }

        boolean availableToBuy = TradingSignalValidator.isAvailableToBuy(tickerPrice24, tradingSignal);
        log.info("Availability to buy for symbol {}: {}", symbol, availableToBuy);

        if (availableToBuy) {
            log.info("Chunked strategy active: scheduling chunked buy orders for {}", symbol);
            this.publisher.publishEvent(new NewChunkedBuyExecutionEvent(this, tradingSignal, tickerPrice24));
        } else {
            log.info("Symbol {} is not available to buy under chunked strategy", symbol);
        }
    }

    private void processException(Exception e) {
        ErrorEvent errorEvent = new ErrorEvent(this, e);
        this.publisher.publishEvent(errorEvent);
    }
}
