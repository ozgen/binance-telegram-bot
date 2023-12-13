package com.ozgen.telegrambinancebot.service;

import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.repository.BuyOrderRepository;
import com.ozgen.telegrambinancebot.repository.SellOrderRepository;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.bot.SellOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BotOrderService {

    private static final Logger log = LoggerFactory.getLogger(BotOrderService.class);

    private final BuyOrderRepository buyOrderRepository;
    private final SellOrderRepository sellOrderRepository;


    public BotOrderService(BuyOrderRepository buyOrderRepository, SellOrderRepository sellOrderRepository) {
        this.buyOrderRepository = buyOrderRepository;
        this.sellOrderRepository = sellOrderRepository;
    }

    public BuyOrder createBuyOrder(BuyOrder buyOrder) {
        return this.buyOrderRepository.save(buyOrder);
    }

    public SellOrder createSellOrder(SellOrder sellOrder) {
        return this.sellOrderRepository.save(sellOrder);
    }

    public BuyOrder getBuyOrder(TradingSignal tradingSignal) {
        return this.buyOrderRepository.findByTradingSignal(tradingSignal).orElse(null);
    }

    public SellOrder getSellOrder(TradingSignal tradingSignal) {
        return this.sellOrderRepository.findByTradingSignal(tradingSignal).orElse(null);
    }

    public List<BuyOrder> getBuyOrders(List<TradingSignal> tradingSignals) {
        return this.buyOrderRepository.findByTradingSignalIn(tradingSignals);
    }
}
