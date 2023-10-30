package org.fdbChallenge;

import com.apple.foundationdb.Database;
import com.apple.foundationdb.FDB;
import com.apple.foundationdb.StreamingMode;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SingleVsMultiRanges {

    private static final int TOTAL_KEYS = 10000;
    private static final int KEYS_PER_RANGE = 1000;
    private static final int NUM_RANGES = TOTAL_KEYS / KEYS_PER_RANGE;

    public static void main(String[] args) {
        FDB fdb = FDB.selectAPIVersion(620);
        Database db = fdb.open();

        // Store 10k key-value pairs
        for (int i = 0; i < TOTAL_KEYS; i++) {
            String key = "key_" + i;
            String value = "val_" + i;
            BasicFDBOps.setKeyValue(db, key, value);
        }

        // Retrieve all 10k key-value pairs using different streaming modes
        for (StreamingMode x : BasicFDBOps.allModes) {
            measureParallelGetRangePerformance(db, x);
        }

        // Delete the key-value pairs to avoid DB bloat
        for (int i = 0; i < TOTAL_KEYS; i++) {
            String key = "key_" + i;
            BasicFDBOps.deleteKeyValue(db, key);
        }
        // Close the database connection
        db.close();
    }
    /**
     * Measures getrange performance on multiple thread
     * @param db db reference
     * @param streamingMode streaming mode
     */
    private static void measureParallelGetRangePerformance(Database db, StreamingMode streamingMode) {
        // Measure multiple getRanges in parallel
        long start = System.currentTimeMillis(); //before threadpool to measure parallelization overhead
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_RANGES);
        for (int i = 0; i < NUM_RANGES; i++) {
            int rangeStart = i * KEYS_PER_RANGE;
            int rangeEnd = rangeStart + KEYS_PER_RANGE;
            executorService.submit(() -> {
                BasicFDBOps.getRange(db, rangeStart, rangeEnd, false, streamingMode);
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Streaming Mode: " + streamingMode + " | Parallel getRange time: " + (end - start) + " ms");
    }
}
