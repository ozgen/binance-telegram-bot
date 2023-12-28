package com.ozgen.telegrambinancebot.utils;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

public class SyncUtilTest {

    @Test
    @Disabled
    public void testPauseBetweenOperations() {
        MockedStatic<SyncUtil> mockedSyncUtil = mockStatic(SyncUtil.class);
        try {
            // Mock behavior
            mockedSyncUtil.when(SyncUtil::pauseBetweenOperations).then(invocation -> null);

            // Invoke the method that uses the static method
            SyncUtil.pauseBetweenOperations();

            // Assertions or verifications here
        } finally {
            mockedSyncUtil.close();
        }
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
