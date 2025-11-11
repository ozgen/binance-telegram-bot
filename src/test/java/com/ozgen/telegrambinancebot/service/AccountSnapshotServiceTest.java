//package com.ozgen.telegrambinancebot.service;
//
//
//import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
//import com.ozgen.telegrambinancebot.repository.SnapshotDataRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//public class AccountSnapshotServiceTest {
//
//    @Mock
//    private SnapshotDataRepository snapshotDataRepository;
//
//    @InjectMocks
//    private AccountSnapshotService accountSnapshotService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testCreateSnapshotData_Success() {
//        SnapshotData snapshotData = new SnapshotData();
//        when(this.snapshotDataRepository.save(any(SnapshotData.class)))
//                .thenReturn(snapshotData);
//
//        SnapshotData result = this.accountSnapshotService.createSnapshotData(snapshotData);
//
//        assertNotNull(result);
//        verify(this.snapshotDataRepository)
//                .save(snapshotData);
//    }
//
//    @Test
//    public void testCreateSnapshotData_Exception() {
//        SnapshotData snapshotData = new SnapshotData();
//        when(this.snapshotDataRepository.save(any(SnapshotData.class)))
//                .thenThrow(new RuntimeException("Test exception"));
//
//        SnapshotData result = this.accountSnapshotService.createSnapshotData(snapshotData);
//
//        assertEquals(snapshotData, result);
//        verify(this.snapshotDataRepository)
//                .save(snapshotData);
//    }
//}
