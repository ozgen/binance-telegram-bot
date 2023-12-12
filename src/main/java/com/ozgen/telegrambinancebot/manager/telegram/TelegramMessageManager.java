package com.ozgen.telegrambinancebot.manager.telegram;

import com.ozgen.telegrambinancebot.adapters.binance.BinanceAPI;
import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.events.IncomingTradingSignalEvent;
import com.ozgen.telegrambinancebot.utils.parser.JsonParser;
import com.ozgen.telegrambinancebot.utils.parser.SignalParser;
import com.ozgen.telegrambinancebot.utils.validators.TradingSignalValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class TelegramMessageManager {

    private static final Logger log = LoggerFactory.getLogger(TelegramMessageManager.class);
    private static final String SUCCESS_MESSAGE = "Received your channel post successfully";
    private static final String FAILED_MESSAGE = "Message is not parsed successfully";

    private final TradingSignalService tradingSignalService;

    private final ApplicationEventPublisher publisher;

    @Autowired
    private  BinanceAPI binanceAPI;



    public TelegramMessageManager(TradingSignalService tradingSignalService, ApplicationEventPublisher publisher) {
        this.tradingSignalService = tradingSignalService;
        this.publisher = publisher;
    }

    public String parseTelegramMessage(String message){
        TradingSignal tradingSignal = SignalParser.parseSignal(message);
        boolean valid = TradingSignalValidator.validate(tradingSignal);
        if(!valid){
            log.warn("invalid data comes from telegram message: '{}'", message);
            return FAILED_MESSAGE;
        }
        tradingSignal.setIsProcessed(ProcessStatus.INIT);
        TradingSignal saved = this.tradingSignalService.saveTradingSignal(tradingSignal);
        IncomingTradingSignalEvent event = new IncomingTradingSignalEvent(this, saved);
        this.publisher.publishEvent(event);
        return SUCCESS_MESSAGE;
    }

    public String testMessage(){
        String accountSnapshot = this.binanceAPI.getOpenOrders("BNBBTC");
        log.info(accountSnapshot);
        try {
            TickerData snapshotData = JsonParser.parseTickerJson(accountSnapshot);
            System.out.print(snapshotData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return SUCCESS_MESSAGE;
    }

}
