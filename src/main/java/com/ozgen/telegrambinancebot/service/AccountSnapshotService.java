package com.ozgen.telegrambinancebot.service;

import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
import com.ozgen.telegrambinancebot.repository.SnapshotDataRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountSnapshotService {
    private static final Logger log = LoggerFactory.getLogger(AccountSnapshotService.class);

    private final SnapshotDataRepository snapshotDataRepository;

    public SnapshotData createSnapshotData(SnapshotData snapshotData) {
        return this.snapshotDataRepository.save(snapshotData);
    }

    public SnapshotData findOne(UUID id) {
        return snapshotDataRepository.findById(id).orElse(null);
    }

}
