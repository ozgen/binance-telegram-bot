package com.ozgen.telegrambinancebot.service;


import com.ozgen.telegrambinancebot.adapters.repository.BuyOrderRepository;
import com.ozgen.telegrambinancebot.adapters.repository.ChunkOrderRepository;
import com.ozgen.telegrambinancebot.adapters.repository.SellOrderRepository;
import com.ozgen.telegrambinancebot.adapters.repository.TradingSignalRepository;
import com.ozgen.telegrambinancebot.model.ExecutionStrategy;
import com.ozgen.telegrambinancebot.model.bot.BuyOrder;
import com.ozgen.telegrambinancebot.model.bot.ChunkOrder;
import com.ozgen.telegrambinancebot.model.bot.OrderStatus;
import com.ozgen.telegrambinancebot.model.bot.SellOrder;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
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
    @Mock
    private ChunkOrderRepository chunkOrderRepository;


    @InjectMocks
    private BotOrderService botOrderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
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

    @Test
    public void testSaveChunkOrder_Success() {
        // Arrange
        ChunkOrder chunkOrder = new ChunkOrder();
        when(chunkOrderRepository.save(chunkOrder)).thenReturn(chunkOrder);

        // Act
        ChunkOrder result = botOrderService.saveChunkOrder(chunkOrder);

        // Assert
        assertNotNull(result);
        assertEquals(chunkOrder, result);
        verify(chunkOrderRepository).save(chunkOrder);
    }

    @Test
    public void testSaveChunkOrder_Exception() {
        // Arrange
        ChunkOrder chunkOrder = new ChunkOrder();
        when(chunkOrderRepository.save(chunkOrder)).thenThrow(new RuntimeException("Test exception"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> botOrderService.saveChunkOrder(chunkOrder));
        verify(chunkOrderRepository).save(chunkOrder);
    }

    @Test
    public void testGetBuyChunksByStatus_Success() {
        // Arrange
        OrderStatus status = OrderStatus.BUY_EXECUTED;
        List<ChunkOrder> expectedChunks = List.of(new ChunkOrder());
        when(chunkOrderRepository.findAllByStatus(status)).thenReturn(expectedChunks);

        // Act
        List<ChunkOrder> result = botOrderService.getBuyChunksByStatus(status);

        // Assert
        assertNotNull(result);
        assertEquals(expectedChunks, result);
        verify(chunkOrderRepository).findAllByStatus(status);
    }

    @Test
    public void testGetBuyChunksByStatus_Exception() {
        // Arrange
        OrderStatus status = OrderStatus.SELL_PENDING_RETRY;
        when(chunkOrderRepository.findAllByStatus(status)).thenThrow(new RuntimeException("Test exception"));

        // Act
        List<ChunkOrder> result = botOrderService.getBuyChunksByStatus(status);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(chunkOrderRepository).findAllByStatus(status);
    }

    @Test
    public void testGetBuyChunksByStatusesAndDate_Success() {
        // Arrange
        List<OrderStatus> statuses = List.of(OrderStatus.SELL_PENDING_RETRY, OrderStatus.BUY_EXECUTED);
        Date date = new Date();
        List<ChunkOrder> expectedChunks = List.of(new ChunkOrder());
        when(chunkOrderRepository.findAllByCreatedAtAfterAndStatusInAndTradingSignal_ExecutionStrategy(date, statuses, ExecutionStrategy.CHUNKED)).thenReturn(expectedChunks);

        // Act
        List<ChunkOrder> result = botOrderService.getBuyChunksByStatusesAndDate(statuses, date);

        // Assert
        assertNotNull(result);
        assertEquals(expectedChunks, result);
        verify(chunkOrderRepository).findAllByCreatedAtAfterAndStatusInAndTradingSignal_ExecutionStrategy(date, statuses, ExecutionStrategy.CHUNKED);
    }

    @Test
    public void testGetBuyChunksByStatusesAndDate_Exception() {
        // Arrange
        List<OrderStatus> statuses = List.of(OrderStatus.SELL_FAILED);
        Date date = new Date();
        when(chunkOrderRepository.findAllByCreatedAtAfterAndStatusInAndTradingSignal_ExecutionStrategy(date, statuses, ExecutionStrategy.CHUNKED)).thenThrow(new RuntimeException("DB error"));

        // Act
        List<ChunkOrder> result = botOrderService.getBuyChunksByStatusesAndDate(statuses, date);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(chunkOrderRepository).findAllByCreatedAtAfterAndStatusInAndTradingSignal_ExecutionStrategy(date, statuses, ExecutionStrategy.CHUNKED);
    }

    @Test
    public void testGetBuyProgressiveChunksByStatusesAndDate_Success() {
        // Arrange
        List<OrderStatus> statuses = List.of(OrderStatus.BUY_EXECUTED);
        Date date = new Date();
        List<ChunkOrder> expectedChunks = List.of(new ChunkOrder());
        when(chunkOrderRepository.findAllByCreatedAtAfterAndStatusInAndTradingSignal_ExecutionStrategy(date, statuses, ExecutionStrategy.CHUNKED_PROGRESSIVE))
                .thenReturn(expectedChunks);

        // Act
        List<ChunkOrder> result = botOrderService.getProgressiveChunksByStatusesAndDate(statuses, date);

        // Assert
        assertNotNull(result);
        assertEquals(expectedChunks, result);
        verify(chunkOrderRepository).findAllByCreatedAtAfterAndStatusInAndTradingSignal_ExecutionStrategy(date, statuses, ExecutionStrategy.CHUNKED_PROGRESSIVE);
    }

    @Test
    public void testGetBuyProgressiveChunksByStatusesAndDate_Exception() {
        // Arrange
        List<OrderStatus> statuses = List.of(OrderStatus.SELL_FAILED);
        Date date = new Date();
        when(chunkOrderRepository.findAllByCreatedAtAfterAndStatusInAndTradingSignal_ExecutionStrategy(date, statuses, ExecutionStrategy.CHUNKED_PROGRESSIVE))
                .thenThrow(new RuntimeException("Simulated DB error"));

        // Act
        List<ChunkOrder> result = botOrderService.getProgressiveChunksByStatusesAndDate(statuses, date);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(chunkOrderRepository).findAllByCreatedAtAfterAndStatusInAndTradingSignal_ExecutionStrategy(date, statuses, ExecutionStrategy.CHUNKED_PROGRESSIVE);
    }

    @Test
    public void testGetChunksBySignalAndStatuses_Success() {
        // Arrange
        String signalId = "test-signal-id";
        List<OrderStatus> statuses = List.of(OrderStatus.SELL_FAILED, OrderStatus.SELL_PENDING_RETRY);
        List<ChunkOrder> expectedChunks = List.of(new ChunkOrder());
        when(chunkOrderRepository.findAllByTradingSignalIdAndStatusIn(signalId, statuses))
                .thenReturn(expectedChunks);

        // Act
        List<ChunkOrder> result = botOrderService.getChunksBySignalAndStatuses(signalId, statuses);

        // Assert
        assertNotNull(result);
        assertEquals(expectedChunks, result);
        verify(chunkOrderRepository).findAllByTradingSignalIdAndStatusIn(signalId, statuses);
    }

    @Test
    public void testGetChunksBySignalAndStatuses_Exception() {
        // Arrange
        String signalId = "faulty-signal-id";
        List<OrderStatus> statuses = List.of(OrderStatus.BUY_FAILED);
        when(chunkOrderRepository.findAllByTradingSignalIdAndStatusIn(signalId, statuses))
                .thenThrow(new RuntimeException("Simulated error"));

        // Act
        List<ChunkOrder> result = botOrderService.getChunksBySignalAndStatuses(signalId, statuses);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(chunkOrderRepository).findAllByTradingSignalIdAndStatusIn(signalId, statuses);
    }
}
