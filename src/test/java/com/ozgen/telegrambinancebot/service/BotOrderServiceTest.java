package com.ozgen.telegrambinancebot.service;


import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.bot.SellOrder;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.repository.BuyOrderRepository;
import com.ozgen.telegrambinancebot.repository.SellOrderRepository;
import com.ozgen.telegrambinancebot.repository.TradingSignalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BotOrderServiceTest {

    @Mock
    private BuyOrderRepository buyOrderRepository;
    @Mock
    private SellOrderRepository sellOrderRepository;
    @Mock
    private TradingSignalRepository tradingSignalRepository;

    @InjectMocks
    private BotOrderService botOrderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateBuyOrder_Success() {
        BuyOrder buyOrder = new BuyOrder();
        TradingSignal tradingSignal = new TradingSignal();
        buyOrder.setTradingSignal(tradingSignal);
        when(this.buyOrderRepository.save(any(BuyOrder.class)))
                .thenReturn(buyOrder);

        BuyOrder result = this.botOrderService.createBuyOrder(buyOrder);

        assertNotNull(result);
        assertEquals(buyOrder, result);
        verify(this.buyOrderRepository)
                .save(buyOrder);
        verify(this.tradingSignalRepository)
                .save(tradingSignal);
    }

    @Test
    public void testCreateBuyOrder_Failure() {
        BuyOrder buyOrder = new BuyOrder();
        TradingSignal tradingSignal = new TradingSignal();
        buyOrder.setTradingSignal(tradingSignal);
        when(this.buyOrderRepository.save(any(BuyOrder.class)))
                .thenThrow(new RuntimeException("Test exception"));

        assertThrows(RuntimeException.class, () -> this.botOrderService.createBuyOrder(buyOrder));
        verify(this.buyOrderRepository)
                .save(buyOrder);
        verify(this.tradingSignalRepository)
                .save(tradingSignal);
    }

    @Test
    public void testCreateSellOrder_Success() {
        SellOrder sellOrder = new SellOrder();
        TradingSignal tradingSignal = new TradingSignal();
        sellOrder.setTradingSignal(tradingSignal);
        when(this.sellOrderRepository.save(any(SellOrder.class)))
                .thenReturn(sellOrder);

        SellOrder result = this.botOrderService.createSellOrder(sellOrder);

        assertNotNull(result);
        assertEquals(sellOrder, result);
        verify(this.sellOrderRepository)
                .save(sellOrder);
        verify(this.tradingSignalRepository)
                .save(tradingSignal);
    }

    @Test
    public void testCreateSellOrder_Failure() {
        SellOrder sellOrder = new SellOrder();
        TradingSignal tradingSignal = new TradingSignal();
        sellOrder.setTradingSignal(tradingSignal);
        when(this.sellOrderRepository.save(any(SellOrder.class)))
                .thenThrow(new RuntimeException("Test exception"));

        assertThrows(RuntimeException.class, () -> this.botOrderService.createSellOrder(sellOrder));
        verify(this.sellOrderRepository)
                .save(sellOrder);
        verify(this.tradingSignalRepository)
                .save(tradingSignal);
    }

    @Test
    public void testGetBuyOrder_Success() {
        TradingSignal signal = new TradingSignal();
        BuyOrder buyOrder = new BuyOrder();
        when(this.buyOrderRepository.findByTradingSignal(signal))
                .thenReturn(Optional.of(buyOrder));

        Optional<BuyOrder> result = this.botOrderService.getBuyOrder(signal);

        assertTrue(result.isPresent());
        assertEquals(buyOrder, result.get());
        verify(this.buyOrderRepository)
                .findByTradingSignal(signal);
    }

    @Test
    public void testGetBuyOrder_NotFound() {
        TradingSignal signal = new TradingSignal();
        when(this.buyOrderRepository.findByTradingSignal(signal))
                .thenReturn(Optional.empty());

        Optional<BuyOrder> result = this.botOrderService.getBuyOrder(signal);

        assertFalse(result.isPresent());
        verify(buyOrderRepository)
                .findByTradingSignal(signal);
    }

    @Test
    public void testGetBuyOrder_Exception() {
        TradingSignal signal = new TradingSignal();
        when(this.buyOrderRepository.findByTradingSignal(signal))
                .thenThrow(new RuntimeException("Test exception"));

        Optional<BuyOrder> result = this.botOrderService.getBuyOrder(signal);

        assertFalse(result.isPresent());
        verify(buyOrderRepository)
                .findByTradingSignal(signal);
    }

    @Test
    public void testGetSellOrder_Success() {
        TradingSignal signal = new TradingSignal();
        SellOrder sellOrder = new SellOrder();
        when(sellOrderRepository.findByTradingSignal(signal))
                .thenReturn(Optional.of(sellOrder));

        Optional<SellOrder> result = this.botOrderService.getSellOrder(signal);

        assertTrue(result.isPresent());
        assertEquals(sellOrder, result.get());
        verify(sellOrderRepository)
                .findByTradingSignal(signal);
    }

    @Test
    public void testGetSellOrder_NotFound() {
        TradingSignal signal = new TradingSignal();
        when(this.sellOrderRepository.findByTradingSignal(signal))
                .thenReturn(Optional.empty());

        Optional<SellOrder> result = this.botOrderService.getSellOrder(signal);

        assertFalse(result.isPresent());
        verify(this.sellOrderRepository)
                .findByTradingSignal(signal);
    }

    @Test
    public void testGetSellOrder_Exception() {
        TradingSignal signal = new TradingSignal();
        when(this.sellOrderRepository.findByTradingSignal(signal))
                .thenThrow(new RuntimeException("Test exception"));

        Optional<SellOrder> result = this.botOrderService.getSellOrder(signal);

        assertFalse(result.isPresent());
        verify(this.sellOrderRepository)
                .findByTradingSignal(signal);
    }

    @Test
    public void testGetBuyOrders_Success() {
        List<TradingSignal> signals = List.of(new TradingSignal());
        List<BuyOrder> expectedOrders = List.of(new BuyOrder());
        when(this.buyOrderRepository.findByTradingSignalIn(signals))
                .thenReturn(expectedOrders);

        List<BuyOrder> result = this.botOrderService.getBuyOrders(signals);

        assertNotNull(result);
        assertEquals(expectedOrders, result);
        verify(this.buyOrderRepository)
                .findByTradingSignalIn(signals);
    }

    @Test
    public void testGetBuyOrders_Exception() {
        List<TradingSignal> signals = List.of(new TradingSignal());
        when(this.buyOrderRepository.findByTradingSignalIn(signals))
                .thenThrow(new RuntimeException("Test exception"));

        List<BuyOrder> result = this.botOrderService.getBuyOrders(signals);

        assertTrue(result.isEmpty());
        verify(buyOrderRepository)
                .findByTradingSignalIn(signals);
    }

    @Test
    public void testGetSellOrders_Success() {
        List<TradingSignal> signals = List.of(new TradingSignal());
        List<SellOrder> expectedOrders = List.of(new SellOrder());
        when(this.sellOrderRepository.findByTradingSignalIn(signals))
                .thenReturn(expectedOrders);

        List<SellOrder> result = this.botOrderService.getSellOrders(signals);

        assertNotNull(result);
        assertEquals(expectedOrders, result);
        verify(this.sellOrderRepository)
                .findByTradingSignalIn(signals);
    }

    @Test
    public void testGetSellOrders_Exception() {
        List<TradingSignal> signals = List.of(new TradingSignal());
        when(this.sellOrderRepository.findByTradingSignalIn(signals))
                .thenThrow(new RuntimeException("Test exception"));

        List<SellOrder> result = this.botOrderService.getSellOrders(signals);

        assertTrue(result.isEmpty());
        verify(this.sellOrderRepository)
                .findByTradingSignalIn(signals);
    }
}
