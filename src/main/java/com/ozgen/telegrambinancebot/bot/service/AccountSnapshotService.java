package com.ozgen.telegrambinancebot.bot.service;

import com.ozgen.telegrambinancebot.bot.repository.SnapshotDataRepository;
import com.ozgen.telegrambinancebot.model.binance.SnapshotData;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountSnapshotService {

    private final SnapshotDataRepository snapshotDataRepository;


    public AccountSnapshotService(SnapshotDataRepository snapshotDataRepository) {
        this.snapshotDataRepository = snapshotDataRepository;
    }

    public SnapshotData createSnapshotData(SnapshotData snapshotData) {
        return this.snapshotDataRepository.save(snapshotData);
    }

    public SnapshotData findOne(UUID id) {
        return snapshotDataRepository.findById(id).orElse(null);
    }

}
