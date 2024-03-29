package com.thread.concurrency.counter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class CounterTest {

    public static Stream<Counter> counterProvider() {
        return Stream.of(new BatchingCounter(), new LockCounter(), new PollingCounter());
    }

    @ParameterizedTest
    @MethodSource("counterProvider")
    public void testAddInMultithreadedEnvironment(
        Counter counter
    ) throws InterruptedException {
        int initialValue = counter.show();
        int nThreads = 100;
        int nAddsPerThread = 1000;
        int valueToAdd = 1;

        try (ExecutorService executorService = Executors.newFixedThreadPool(nThreads)) {
            for (int i = 0; i < nThreads; i++) {
                executorService.submit(() -> {
                    for (int j = 0; j < nAddsPerThread; j++) {
                        counter.add(valueToAdd);
                    }
                });
            }
            if (counter instanceof BatchingCounter) {
                Thread.sleep(500);
            }
        }

        int expectedValue = initialValue + nThreads * nAddsPerThread * valueToAdd;
        assertEquals(expectedValue, counter.show());
    }

    @Test
    public void testMalfunction() {
        var counter = new BasicCounter();
        int initialValue = counter.show();
        int nThreads = 100;
        int nAddsPerThread = 1000;
        int valueToAdd = 1;

        try (ExecutorService executorService = Executors.newFixedThreadPool(nThreads)) {
            for (int i = 0; i < nThreads; i++) {
                executorService.submit(() -> {
                    for (int j = 0; j < nAddsPerThread; j++) {
                        counter.add(valueToAdd);
                    }
                });
            }
        }

        int expectedValue = initialValue + nThreads * nAddsPerThread * valueToAdd;
        assertNotEquals(expectedValue, counter.show());
    }
}
