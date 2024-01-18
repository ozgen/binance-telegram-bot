package com.ozgen.telegrambinancebot.manager.telegram;

import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.model.events.IncomingTradingSignalEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import com.ozgen.telegrambinancebot.utils.parser.SignalParser;
import com.ozgen.telegrambinancebot.utils.validators.TradingSignalValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramMessageManager {

    private static final Logger log = LoggerFactory.getLogger(TelegramMessageManager.class);
    static final String SUCCESS_MESSAGE = "The post with %s has been received successfully";
    static final String FAILED_MESSAGE = "The post is not parsed.";

    private final TradingSignalService tradingSignalService;

    private final ApplicationEventPublisher publisher;

//    @Autowired
//    private  BinanceAPI binanceAPI;


    public String parseTelegramMessage(String message) {
        TradingSignal tradingSignal = SignalParser.parseSignal(message);
        boolean valid = TradingSignalValidator.validate(tradingSignal);
        if (!valid) {
            log.warn("invalid data comes from telegram message: '{}'", message);
            return FAILED_MESSAGE;
        }
        tradingSignal.setIsProcessed(ProcessStatus.INIT);
        TradingSignal saved = this.tradingSignalService.saveTradingSignal(tradingSignal);
        IncomingTradingSignalEvent event = new IncomingTradingSignalEvent(this, saved);
        this.publisher.publishEvent(event);

        return String.format(SUCCESS_MESSAGE, saved.getSymbol());
    }

//    public String testMessage(){
//        String accountSnapshot = this.binanceAPI.getOpenOrders("BNBBTC");
//        log.info(accountSnapshot);
//        try {
//            TickerData snapshotData = JsonParser.parseTickerJson(accountSnapshot);
//            System.out.print(snapshotData);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        return SUCCESS_MESSAGE;
//    }

}
