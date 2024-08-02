package com.leuw.reactivebroadcastingservice.util;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

//Needs work
@SpringBootTest
public class LockFreeHashTableTest {

    private static final int NUM_THREADS = 8;
    private static final int NUM_ENTRIES = 1000;
    private static final int READ_WRITE_RATIO = 10; //simulate a read heavy workload

    @Test
    public void testCorrectness() {
        LockFreeHashTable<Integer, String> hashTable = new LockFreeHashTable<>(128);

        // Insert entries
        IntStream.range(0, NUM_ENTRIES).forEach(i -> hashTable.put(i, "Value" + i));

        // Verify entries
        boolean passed = IntStream.range(0, NUM_ENTRIES)
                .allMatch(i -> ("Value" + i).equals(hashTable.get(i)));

        assertTrue(passed, "Correctness test FAILED");
    }

    @Test
    public void testPerformance() throws InterruptedException {
        LockFreeHashTable<Integer, String> hashTable = new LockFreeHashTable<>(128);

        // Insert initial entries
        IntStream.range(0, NUM_ENTRIES).forEach(i -> hashTable.put(i, "Value" + i));

        AtomicInteger reads = new AtomicInteger();
        AtomicInteger writes = new AtomicInteger();

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        long startTime = System.nanoTime();

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                if (ThreadLocalRandom.current().nextInt(READ_WRITE_RATIO) == 0) {
                    int key = ThreadLocalRandom.current().nextInt(NUM_ENTRIES);
                    hashTable.put(key, "NewValue" + key);
                    writes.incrementAndGet();
                } else {
                    int key = ThreadLocalRandom.current().nextInt(NUM_ENTRIES);
                    String value = hashTable.get(key);
                    if (value != null) {
                        // Simulate some arbitrary operation with the value
                        int length = value.length();
                    }
                    reads.incrementAndGet();
                }
            }
        };

        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(task);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES );

        long endTime = System.nanoTime();

        long duration = endTime - startTime;
        double seconds = duration / 1_000_000_000.0;
        int totalOperations = reads.get() + writes.get();

        System.out.printf("Performance test completed in %.2f seconds%n", seconds);
        System.out.printf("Total operations: %d (%.2f ops/sec)%n", totalOperations, totalOperations / seconds);
        System.out.printf("Reads: %d, Writes: %d%n", reads.get(), writes.get());

        assertTrue(totalOperations > 0, "Performance test did not perform any operations");
    }
}
