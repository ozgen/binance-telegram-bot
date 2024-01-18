package com.ozgen.telegrambinancebot.service;


import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.model.bot.FutureTrade;
import com.ozgen.telegrambinancebot.model.telegram.TradingSignal;
import com.ozgen.telegrambinancebot.repository.FutureTradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FutureTradeServiceTest {

    @Mock
    private FutureTradeRepository futureTradeRepository;

    @InjectMocks
    private FutureTradeService futureTradeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateFutureTrade_Success() {
        FutureTrade futureTrade = new FutureTrade();
        when(this.futureTradeRepository.save(any(FutureTrade.class)))
                .thenReturn(futureTrade);

        FutureTrade result = this.futureTradeService.createFutureTrade(futureTrade);

        assertNotNull(result);
        assertEquals(futureTrade, result);
        verify(futureTradeRepository)
                .save(futureTrade);
    }

    @Test
    public void testCreateFutureTrade_Failure() {
        FutureTrade futureTrade = new FutureTrade();
        when(futureTradeRepository.save(any(FutureTrade.class)))
                .thenThrow(new RuntimeException("Test exception"));

        assertThrows(RuntimeException.class, () -> this.futureTradeService.createFutureTrade(futureTrade));
        verify(futureTradeRepository)
                .save(futureTrade);
    }

    @Test
    public void testCreateFutureTradeWithSignalAndStatus_Success() {
        TradingSignal tradingSignal = new TradingSignal();
        String signalId = UUID.randomUUID().toString();
        tradingSignal.setId(signalId);
        TradeStatus status = TradeStatus.ERROR_BUY;
        when(this.futureTradeRepository.save(any(FutureTrade.class)))
                .thenAnswer((Answer<FutureTrade>) invocation -> (FutureTrade) invocation.getArguments()[0]);

        FutureTrade createdTrade = this.futureTradeService.createFutureTrade(tradingSignal, status);

        verify(this.futureTradeRepository)
                .save(argThat(futureTrade ->
                        futureTrade.getTradeSignalId().equals(signalId) &&
                                futureTrade.getTradeStatus() == status));
        assertEquals(signalId, createdTrade.getTradeSignalId());
        assertEquals(status, createdTrade.getTradeStatus());
    }

    @Test
    public void testCreateFutureTradeWithSignalAndStatus_FailedReturnNull() {
        TradingSignal tradingSignal = new TradingSignal();
        tradingSignal.setId(null);
        TradeStatus status = TradeStatus.ERROR_BUY;
        FutureTrade createdTrade = this.futureTradeService.createFutureTrade(tradingSignal, status);

        verify(this.futureTradeRepository, never())
                .save(any());
        assertNull(createdTrade);
    }

    @Test
    public void testGetAllFutureTradeByTradeStatus_Success() {
        TradeStatus tradeStatus = TradeStatus.ERROR_BUY;
        List<FutureTrade> expectedTrades = List.of(new FutureTrade());
        when(this.futureTradeRepository.findByTradeStatus(tradeStatus))
                .thenReturn(expectedTrades);

        List<FutureTrade> result = this.futureTradeService.getAllFutureTradeByTradeStatus(tradeStatus);

        assertEquals(expectedTrades, result);
        verify(futureTradeRepository)
                .findByTradeStatus(tradeStatus);
    }

    @Test
    public void testGetAllFutureTradeByTradingSignals_Success() {
        List<String> uuidList = List.of(UUID.randomUUID().toString());
        List<FutureTrade> expectedTrades = List.of(new FutureTrade());
        when(this.futureTradeRepository.findAllByTradeSignalIdIn(uuidList))
                .thenReturn(expectedTrades);

        List<FutureTrade> result = this.futureTradeService.getAllFutureTradeByTradingSignals(uuidList);

        assertEquals(expectedTrades, result);
        verify(this.futureTradeRepository)
                .findAllByTradeSignalIdIn(uuidList);
    }

    @Test
    public void testDeleteFutureTrades_Success() {
        List<FutureTrade> futureTrades = List.of(new FutureTrade());

        this.futureTradeService.deleteFutureTrades(futureTrades);

        verify(this.futureTradeRepository)
                .deleteAll(futureTrades);
    }
}
