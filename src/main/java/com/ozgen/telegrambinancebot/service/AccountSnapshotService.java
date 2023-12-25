package com.ozgen.telegrambinancebot.service;

import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
import com.ozgen.telegrambinancebot.repository.SnapshotDataRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountSnapshotService {
    private static final Logger log = LoggerFactory.getLogger(AccountSnapshotService.class);

    private final SnapshotDataRepository snapshotDataRepository;

    public SnapshotData createSnapshotData(SnapshotData snapshotData) {
        try {
            SnapshotData saved = this.snapshotDataRepository.save(snapshotData);
            log.info("Snapshot created: {}", saved);
            return saved;
        } catch (Exception e) {
            log.error("Error creating snapshot: {}", e.getMessage(), e);
            return snapshotData;
        }
    }
}
