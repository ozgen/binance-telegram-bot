package com.ozgen.telegrambinancebot.adapters.repository;


import com.ozgen.telegrambinancebot.model.TradeStatus;
import com.ozgen.telegrambinancebot.model.bot.FutureTrade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaRepositories(basePackages = "com.ozgen.telegrambinancebot.adapters.repository")
public class FutureTradeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FutureTradeRepository futureTradeRepository;

    private FutureTrade trade1, trade2, trade3;
    private String signalId1, signalId2;

    @BeforeEach
    public void setUp() {
        this.signalId1 = UUID.randomUUID().toString();
        this.signalId2 = UUID.randomUUID().toString();

        this.trade1 = new FutureTrade();
        this.trade1.setTradeStatus(TradeStatus.INSUFFICIENT);
        this.trade1.setTradeSignalId(signalId1);
        this.entityManager.persist(trade1);

        this.trade2 = new FutureTrade();
        this.trade2.setTradeStatus(TradeStatus.NOT_IN_RANGE);
        this.trade2.setTradeSignalId(this.signalId1);
        this.entityManager.persist(this.trade2);

        this.trade3 = new FutureTrade();
        this.trade3.setTradeStatus(TradeStatus.INSUFFICIENT);
        this.trade3.setTradeSignalId(this.signalId2);
        this.entityManager.persist(this.trade3);
    }

    @Test
    public void testFindByTradeStatus() {
        List<FutureTrade> foundTrades =  this.futureTradeRepository.findByTradeStatus(TradeStatus.INSUFFICIENT);
        assertThat(foundTrades).containsExactlyInAnyOrder(this.trade1, this.trade3);
    }

    @Test
    public void testFindByTradeSignalId() {
        List<FutureTrade> foundTrades = this.futureTradeRepository.findByTradeSignalId(this.signalId1);
        assertThat(foundTrades).containsExactlyInAnyOrder(this.trade1, this.trade2);
    }

    @Test
    public void testFindAllByTradeSignalIdIn() {
        List<FutureTrade> foundTrades = this.futureTradeRepository.findAllByTradeSignalIdIn(List.of(this.signalId1, this.signalId2));
        assertThat(foundTrades).containsExactlyInAnyOrder(this.trade1, this.trade2, this.trade3);
    }
}
