package com.ozgen.telegrambinancebot.manager.telegram;

import com.ozgen.telegrambinancebot.configuration.properties.AppConfiguration;
import com.ozgen.telegrambinancebot.model.ExecutionStrategy;
import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.model.events.IncomingChunkedTradingSignalEvent;
import com.ozgen.telegrambinancebot.model.events.IncomingProgressiveChunkedTradingSignalEvent;
import com.ozgen.telegrambinancebot.model.events.IncomingTradingSignalEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import com.ozgen.telegrambinancebot.utils.parser.SignalParser;
import com.ozgen.telegrambinancebot.utils.validators.TradingSignalValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramMessageManager {

    static final String SUCCESS_MESSAGE = "The post with %s has been received successfully";
    static final String FAILED_MESSAGE = "The post is not parsed.";

    private final TradingSignalService tradingSignalService;
    private final AppConfiguration appConfiguration;

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
        tradingSignal.setExecutionStrategy(appConfiguration.getStrategy());

        TradingSignal saved = this.tradingSignalService.saveTradingSignal(tradingSignal);
        if (saved.getExecutionStrategy() == ExecutionStrategy.CHUNKED) {
            log.info("Dispatching IncomingChunkedTradingSignalEvent for {}", saved.getSymbol());
            this.publisher.publishEvent(new IncomingChunkedTradingSignalEvent(this, saved));
        } else if (saved.getExecutionStrategy() == ExecutionStrategy.CHUNKED_PROGRESSIVE) {
            log.info("Dispatching IncomingProgressiveChunkedTradingSignalEvent for {}", saved.getSymbol());
            this.publisher.publishEvent(new IncomingProgressiveChunkedTradingSignalEvent(this, saved));
        } else {
            log.info("Dispatching IncomingTradingSignalEvent for {}", saved.getSymbol());
            this.publisher.publishEvent(new IncomingTradingSignalEvent(this, saved));
        }

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
