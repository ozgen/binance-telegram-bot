package com.ozgen.telegrambinancebot.manager.binance;

import com.ozgen.telegrambinancebot.configuration.properties.ScheduleConfiguration;
import com.ozgen.telegrambinancebot.model.ProcessStatus;
import com.ozgen.telegrambinancebot.model.binance.TickerData;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.events.ErrorEvent;
import com.ozgen.telegrambinancebot.model.events.NewSellOrderEvent;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.service.BotOrderService;
import com.ozgen.telegrambinancebot.service.TradingSignalService;
import com.ozgen.telegrambinancebot.utils.DateFactory;
import com.ozgen.telegrambinancebot.utils.SyncUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceSellLaterManager {
    private final TradingSignalService tradingSignalService;
    private final BotOrderService botOrderService;
    private final ApplicationEventPublisher publisher;
    private final ScheduleConfiguration scheduleConfiguration;
    private final BinanceHelper binanceHelper;
    private final BinanceApiManager binanceApiManager;

    public void processSellLaterOrders() {
        Date searchDate = getSearchDate();
        List<TradingSignal> tradingSignals = this.tradingSignalService
                .getSellLaterTradingSignalsAfterDateAndIsProcessIn(searchDate, List.of(ProcessStatus.BUY));
        if (tradingSignals.isEmpty()) {
            log.info("No trading signal has been detected.");
            return;
        }

        List<BuyOrder> buyOrders = this.botOrderService.getBuyOrders(tradingSignals);
        buyOrders.forEach(this::processSellLaterOrder);
    }

    private void processSellLaterOrder(BuyOrder buyOrder) {
        try {
            TickerData tickerPrice24 = this.binanceApiManager.getTickerPrice24(buyOrder.getSymbol());
            if (tickerPrice24 == null) {
                log.warn("Ticker price is null for symbol: {}", buyOrder.getSymbol());
                return;
            }
            if (this.binanceHelper.isCoinPriceAvailableToSell(buyOrder.getBuyPrice(), tickerPrice24)) {
                log.info("Coin price is available to sell for symbol: {}", buyOrder.getSymbol());
                this.safelyPublishNewSellOrder(buyOrder);
            } else {
                log.info("Coin price is not available to sell for symbol: {}", buyOrder.getSymbol());
            }
        } catch (Exception e) {
            log.error("Error processing sell later order for symbol: {}", buyOrder.getSymbol(), e);
            throw new RuntimeException(e);
        }
    }


    private void safelyPublishNewSellOrder(BuyOrder buyOrder) {
        try {
            this.publishNewSellOrder(buyOrder);
            SyncUtil.pauseBetweenOperations();
        } catch (Exception e) {
            log.error("Error while processing sell order for BuyOrder ID {}: {}", buyOrder.getId(), e.getMessage(), e);
            this.processException(e);
        }
    }

    private void publishNewSellOrder(BuyOrder buyOrder) {
        NewSellOrderEvent newSellOrderEvent = new NewSellOrderEvent(this, buyOrder);
        this.publisher.publishEvent(newSellOrderEvent);
        log.info("Published NewSellOrderEvent for BuyOrder {}", buyOrder.getId());
    }

    private Date getSearchDate() {
        return DateFactory.getDateBeforeInMonths(this.scheduleConfiguration.getMonthBefore());
    }

    private void processException(Exception e) {
        ErrorEvent errorEvent = new ErrorEvent(this, e);
        this.publisher.publishEvent(errorEvent);
    }
}
