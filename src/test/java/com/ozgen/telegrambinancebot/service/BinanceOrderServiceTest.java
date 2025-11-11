package com.ozgen.telegrambinancebot.service;


import com.ozgen.telegrambinancebot.model.binance.CancelAndNewOrderResponse;
import com.ozgen.telegrambinancebot.model.binance.OpenOrder;
import com.ozgen.telegrambinancebot.model.binance.OrderInfo;
import com.ozgen.telegrambinancebot.model.binance.OrderResponse;
import com.ozgen.telegrambinancebot.adapters.repository.CancelAndNewOrderResponseRepository;
import com.ozgen.telegrambinancebot.adapters.repository.OpenOrderRepository;
import com.ozgen.telegrambinancebot.adapters.repository.OrderInfoRepository;
import com.ozgen.telegrambinancebot.adapters.repository.OrderResponseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BinanceOrderServiceTest {
    @Mock
    private OrderInfoRepository orderInfoRepository;
    @Mock
    private OpenOrderRepository openOrderRepository;
    @Mock
    private OrderResponseRepository orderResponseRepository;
    @Mock
    private CancelAndNewOrderResponseRepository cancelAndNewOrderResponseRepository;

    @InjectMocks
    private BinanceOrderService binanceOrderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOpenOrders_Success() {
        List<OpenOrder> openOrders = List.of(new OpenOrder());
        when(this.openOrderRepository.saveAll(openOrders))
                .thenReturn(openOrders);

        List<OpenOrder> result = this.binanceOrderService.createOpenOrders(openOrders);

        assertNotNull(result);
        verify(this.openOrderRepository)
                .saveAll(openOrders);
    }

    @Test
    public void testCreateOrderInfos_Success() {
        List<OrderInfo> orderInfos = List.of(new OrderInfo());
        when(this.orderInfoRepository.saveAll(orderInfos))
                .thenReturn(orderInfos);

        List<OrderInfo> result = this.binanceOrderService.createOrderInfos(orderInfos);

        assertNotNull(result);
        verify(this.orderInfoRepository)
                .saveAll(orderInfos);
    }

    @Test
    public void testCreateOrderInfos_Exception() {
        List<OrderInfo> orderInfos = List.of(new OrderInfo());
        when(this.orderInfoRepository.saveAll(orderInfos))
                .thenThrow(new RuntimeException("Test exception"));

        List<OrderInfo> result = this.binanceOrderService.createOrderInfos(orderInfos);

        assertEquals(orderInfos, result);
        verify(this.orderInfoRepository)
                .saveAll(orderInfos);
    }

    @Test
    public void testCreateOrderResponse_Success() {
        OrderResponse orderResponse = new OrderResponse();
        when(this.orderResponseRepository.save(orderResponse))
                .thenReturn(orderResponse);

        OrderResponse result = this.binanceOrderService.createOrderResponse(orderResponse);

        assertNotNull(result);
        verify(this.orderResponseRepository)
                .save(orderResponse);
    }

    @Test
    public void testCreateOrderResponse_Exception() {
        OrderResponse orderResponse = new OrderResponse();
        when(this.orderResponseRepository.save(orderResponse))
                .thenThrow(new RuntimeException("Test exception"));

        OrderResponse result = this.binanceOrderService.createOrderResponse(orderResponse);

        assertEquals(orderResponse, result);
        verify(this.orderResponseRepository)
                .save(orderResponse);
    }

    @Test
    public void testCreateCancelAndNewOrderResponse_Success() {
        CancelAndNewOrderResponse response = new CancelAndNewOrderResponse();
        when(this.cancelAndNewOrderResponseRepository.save(response))
                .thenReturn(response);

        CancelAndNewOrderResponse result = this.binanceOrderService.createCancelAndNewOrderResponse(response);

        assertNotNull(result);
        verify(this.cancelAndNewOrderResponseRepository)
                .save(response);
    }

    @Test
    public void testCreateCancelAndNewOrderResponse_Exception() {
        CancelAndNewOrderResponse response = new CancelAndNewOrderResponse();
        when(this.cancelAndNewOrderResponseRepository.save(response))
                .thenThrow(new RuntimeException("Test exception"));

        CancelAndNewOrderResponse result = this.binanceOrderService.createCancelAndNewOrderResponse(response);

        assertEquals(response, result);
        verify(this.cancelAndNewOrderResponseRepository)
                .save(response);
    }

}
