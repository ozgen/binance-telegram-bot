package com.ozgen.telegrambinancebot.service;

import com.ozgen.telegrambinancebot.repository.OpenOrderRepository;
import com.ozgen.telegrambinancebot.repository.OrderInfoRepository;
import com.ozgen.telegrambinancebot.repository.OrderResponseRepository;
import com.ozgen.telegrambinancebot.model.binance.OpenOrder;
import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
import com.ozgen.telegrambinancebot.model.binance.OrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BinanceOrderService {
    private static final Logger log = LoggerFactory.getLogger(BinanceOrderService.class);

    private final OrderInfoRepository orderInfoRepository;
    private final OpenOrderRepository openOrderRepository;
    private final OrderResponseRepository orderResponseRepository;

    public BinanceOrderService(OrderInfoRepository orderInfoRepository, OpenOrderRepository openOrderRepository,
                               OrderResponseRepository orderResponseRepository) {
        this.orderInfoRepository = orderInfoRepository;
        this.openOrderRepository = openOrderRepository;
        this.orderResponseRepository = orderResponseRepository;
    }

    public List<OpenOrder> createOpenOrders(List<OpenOrder> openOrderList) {

        return this.openOrderRepository.saveAll(openOrderList);
    }

    public List<OrderInfo> createOrderInfos(List<OrderInfo> orderInfoList) {
        return this.orderInfoRepository.saveAll(orderInfoList);
    }

    public OrderResponse createOrderResponse(OrderResponse orderResponse) {
        return this.orderResponseRepository.save(orderResponse);
    }

    public OrderInfo findOrderInfo(UUID id) {
        return this.orderInfoRepository.findById(id).orElse(null);
    }

    public OrderResponse findOrderResponse(UUID id) {
        return this.orderResponseRepository.findById(id).orElse(null);
    }

    public OpenOrder findOpenOrder(UUID id) {
        return this.openOrderRepository.findById(id).orElse(null);
    }
}
