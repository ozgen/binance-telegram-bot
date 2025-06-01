package com.ozgen.telegrambinancebot.service;

import com.ozgen.telegrambinancebot.model.binance.CancelAndNewOrderResponse;
import com.ozgen.telegrambinancebot.model.binance.OpenOrder;
import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
import com.ozgen.telegrambinancebot.model.binance.OrderResponse;
import com.ozgen.telegrambinancebot.adapters.repository.CancelAndNewOrderResponseRepository;
import com.ozgen.telegrambinancebot.adapters.repository.OpenOrderRepository;
import com.ozgen.telegrambinancebot.adapters.repository.OrderInfoRepository;
import com.ozgen.telegrambinancebot.adapters.repository.OrderResponseRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BinanceOrderService {
    private static final Logger log = LoggerFactory.getLogger(BinanceOrderService.class);

    private final OrderInfoRepository orderInfoRepository;
    private final OpenOrderRepository openOrderRepository;
    private final OrderResponseRepository orderResponseRepository;
    private final CancelAndNewOrderResponseRepository cancelAndNewOrderResponseRepository;


    public List<OpenOrder> createOpenOrders(List<OpenOrder> openOrderList) {

        return this.openOrderRepository.saveAll(openOrderList);
    }

    public List<OrderInfo> createOrderInfos(List<OrderInfo> orderInfoList) {
        try {
            if (orderInfoList.isEmpty()){
                return orderInfoList;
            }
            List<OrderInfo> saved = this.orderInfoRepository.saveAll(orderInfoList);
            log.info("Order info list created: {}", saved);
            return saved;
        } catch (Exception e) {
            log.error("Error saving order info list: {}", e.getMessage(), e);
            return orderInfoList;
        }
    }

    public OrderResponse createOrderResponse(OrderResponse orderResponse) {
        try {
            OrderResponse saved = this.orderResponseRepository.save(orderResponse);
            log.info("order response created: {}", saved);
            return saved;
        } catch (Exception e) {
            log.error("Error creating order response: {}", e.getMessage(), e);
            return orderResponse;
        }
    }

    public CancelAndNewOrderResponse createCancelAndNewOrderResponse(CancelAndNewOrderResponse orderResponse) {
        try {
            CancelAndNewOrderResponse saved = this.cancelAndNewOrderResponseRepository.save(orderResponse);
            log.info("CancelAndNewOrderResponse created: {}", saved);
            return saved;
        } catch (Exception e) {
            log.error("Error creating CancelAndNewOrderResponse: {}", e.getMessage(), e);
            return orderResponse;
        }
    }
}
