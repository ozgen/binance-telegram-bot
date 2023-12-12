package com.ozgen.telegrambinancebot.bot.service;

import com.ozgen.telegrambinancebot.bot.repository.BuyOrderRepository;
import com.ozgen.telegrambinancebot.bot.repository.SellOrderRepository;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.bot.SellOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BotOrderService {

    private static final Logger log = LoggerFactory.getLogger(BotOrderService.class);

    private final BuyOrderRepository buyOrderRepository;
    private final SellOrderRepository sellOrderRepository;


    public BotOrderService(BuyOrderRepository buyOrderRepository, SellOrderRepository sellOrderRepository) {
        this.buyOrderRepository = buyOrderRepository;
        this.sellOrderRepository = sellOrderRepository;
    }

    public BuyOrder createBuyOrder(BuyOrder buyOrder){
        return this.buyOrderRepository.save(buyOrder);
    }

    public SellOrder createSellOrder(SellOrder sellOrder){
        return this.sellOrderRepository.save(sellOrder);
    }
}
