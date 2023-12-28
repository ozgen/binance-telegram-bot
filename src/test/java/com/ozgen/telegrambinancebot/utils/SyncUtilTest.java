package com.ozgen.telegrambinancebot.utils;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SyncUtilTest {

    @Test
    public void testPauseBetweenOperations() {
        long startTime = System.currentTimeMillis();
        SyncUtil.pauseBetweenOperations();
        long endTime = System.currentTimeMillis();

        // Check that the time elapsed is approximately 5 seconds
        long elapsedTime = endTime - startTime;
        assertTrue(elapsedTime >= 4000, "Elapsed time should be at least 5 seconds");
    }

    @Test
    public void testPauseBetweenOperationsHandlesInterruption() throws Exception {
        Thread testThread = new Thread(SyncUtil::pauseBetweenOperations);
        SyncUtil.wasInterrupted = false; // Reset the flag before the test
        testThread.start();

        // Ensure the thread has started and is likely in the sleep state
        Thread.sleep(1000); // Waiting 1 second should be enough for the thread to start and enter sleep

        // Interrupt the thread
        testThread.interrupt();

        // Wait for the thread to finish execution
        testThread.join();

        // Assert that the method caught an InterruptedException
        assertTrue(SyncUtil.wasInterrupted);
    }
}
