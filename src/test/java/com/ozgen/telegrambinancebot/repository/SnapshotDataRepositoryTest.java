package com.ozgen.telegrambinancebot.repository;

import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
import com.ozgen.telegrambinancebot.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class SnapshotDataRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SnapshotDataRepository snapshotDataRepository;

    private final double BNB_AMOUNT = 0.25760623;
    private final String BNB = "BNB";

    @BeforeEach
    public void setUp() throws Exception {
        SnapshotData snapshotData = TestData.getSnapshotData();
        this.entityManager.persist(snapshotData);
    }

    @Test
    public void testFindAll() {
        List<SnapshotData> snapshotDataList = this.snapshotDataRepository.findAll();

        assertFalse(snapshotDataList.isEmpty());
        assertEquals(1, snapshotDataList.size());
        SnapshotData snapshotData = snapshotDataList.get(0);
        assertEquals(BNB_AMOUNT, snapshotData.getCoinValue(BNB));
    }
}
