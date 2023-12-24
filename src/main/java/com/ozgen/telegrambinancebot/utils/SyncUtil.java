package com.ozgen.telegrambinancebot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class SyncUtil {

    private static final Logger log = LoggerFactory.getLogger(SyncUtil.class);

    public static void pauseBetweenOperations() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted during pause", e);
        }
    }
}
