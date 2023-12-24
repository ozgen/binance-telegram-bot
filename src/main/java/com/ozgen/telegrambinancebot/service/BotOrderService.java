package com.ozgen.telegrambinancebot.service;

import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.repository.BuyOrderRepository;
import com.ozgen.telegrambinancebot.repository.SellOrderRepository;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.bot.SellOrder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BotOrderService {

    private static final Logger log = LoggerFactory.getLogger(BotOrderService.class);

    private final BuyOrderRepository buyOrderRepository;
    private final SellOrderRepository sellOrderRepository;


    @Transactional
    public BuyOrder createBuyOrder(BuyOrder buyOrder) {
        try {
            BuyOrder savedOrder = buyOrderRepository.save(buyOrder);
            log.info("Buy order created: {}", savedOrder);
            return savedOrder;
        } catch (Exception e) {
            log.error("Error creating buy order: {}", e.getMessage(), e);
            throw e; // Rethrow to handle at a higher level or use specific business logic
        }
    }

    @Transactional
    public SellOrder createSellOrder(SellOrder sellOrder) {
        try {
            SellOrder savedOrder = sellOrderRepository.save(sellOrder);
            log.info("Sell order created: {}", savedOrder);
            return savedOrder;
        } catch (Exception e) {
            log.error("Error creating sell order: {}", e.getMessage(), e);
            throw e; // Rethrow to handle at a higher level or use specific business logic
        }
    }

    public Optional<BuyOrder> getBuyOrder(TradingSignal tradingSignal) {
        try {
            Optional<BuyOrder> buyOrder = buyOrderRepository.findByTradingSignal(tradingSignal);
            log.info("Retrieved buy order for trading signal ID {}: {}", tradingSignal.getId(), buyOrder);
            return buyOrder;
        } catch (Exception e) {
            log.error("Error retrieving buy order for trading signal ID {}: {}", tradingSignal.getId(), e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Optional<SellOrder> getSellOrder(TradingSignal tradingSignal) {
        try {
            Optional<SellOrder> sellOrder = sellOrderRepository.findByTradingSignal(tradingSignal);
            log.info("Retrieved sell order for trading signal ID {}: {}", tradingSignal.getId(), sellOrder);
            return sellOrder;
        } catch (Exception e) {
            log.error("Error retrieving sell order for trading signal ID {}: {}", tradingSignal.getId(), e.getMessage(), e);
            return Optional.empty();
        }
    }

    public List<BuyOrder> getBuyOrders(List<TradingSignal> tradingSignals) {
        try {
            List<BuyOrder> buyOrders = buyOrderRepository.findByTradingSignalIn(tradingSignals);
            log.info("Retrieved {} buy orders for trading signals", buyOrders.size());
            return buyOrders;
        } catch (Exception e) {
            log.error("Error retrieving buy orders: {}", e.getMessage(), e);
            return List.of(); // Return an empty list in case of error
        }
    }
}
