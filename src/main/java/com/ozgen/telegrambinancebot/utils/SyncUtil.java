package com.ozgen.telegrambinancebot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class SyncUtil {

    private static final Logger log = LoggerFactory.getLogger(SyncUtil.class);

    public static volatile boolean wasInterrupted = false;


    public static void pauseBetweenOperations() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            wasInterrupted = true;
            Thread.currentThread().interrupt();
            log.error("Thread interrupted during pause", e);
        }
    }
}
