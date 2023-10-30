package org.fdbChallenge;

import com.apple.foundationdb.Database;
import com.apple.foundationdb.FDB;
import com.apple.foundationdb.StreamingMode;

public class SingleGetRange {

    private static final int TOTAL_KEYS = 10000;

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
            measureGetRangePerformance(db, x);
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
     * Measures getrange performance on one thread
     * @param db db reference
     * @param streamingMode streaming mode
     */
    private static void measureGetRangePerformance(Database db, StreamingMode streamingMode) {
        long startTime = System.currentTimeMillis();
        BasicFDBOps.getRange(db, TOTAL_KEYS, false, streamingMode);
        long endTime = System.currentTimeMillis();

        System.out.println("Streaming Mode: " + streamingMode + ", Time Taken: " + (endTime - startTime) + " ms");
    }
}

