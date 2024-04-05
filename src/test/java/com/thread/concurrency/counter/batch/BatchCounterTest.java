package com.thread.concurrency.counter.batch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BatchCounterTest {
    private final List<Integer> numbers;
    private final Runnable defaultTask;
    private final int partialSum;
    private BatchCounter counter;

    public BatchCounterTest() {
        this.numbers = range();
        this.defaultTask = () -> {
            for (Integer number : numbers) {
                counter.add(number);
            }
            counter.flush();
        };
        this.partialSum = numbers.stream().reduce(0, Integer::sum);
    }

    private static List<Integer> range() {
        return IntStream.range(0, 1000).boxed().collect(Collectors.toList());
    }

    private static List<Integer> range(int numberOfThreads, int expected) {
        int baseValue = expected / numberOfThreads;
        int remainder = expected % numberOfThreads;

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            if (i < remainder) {
                result.add(baseValue + 1);
            } else {
                result.add(baseValue);
            }
        }
        return result;
    }

    @BeforeEach
    void setUp() {
        this.counter = new ConcurrentBatchingCounter();
    }


    @Test
    void singleThreading() {
        defaultTask.run();
        assertEquals(partialSum, counter.show());
    }


    @Test
    void conditionalMultiThreading() {
        // given
        int numberOfThreads = 2;
        int expected = Integer.MAX_VALUE / 1024;
        List<Integer> iterPerThread = range(numberOfThreads, expected);
        Consumer<Integer> task = (Integer number) -> {
            for (int i = 0; i < number; i++) {
                counter.add(1);
            }
            counter.flush();
        };
        // when
        try (ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads)) {
            for (int num : iterPerThread) {
                executor.submit(() -> task.accept(num));
            }
        }
        // then
        assertEquals(expected, counter.show());
    }

    @Test
    void conditionalAsyncVirtualMultiThreading() {
        // given
        int numberOfThreads = 2;
        int expected = Integer.MAX_VALUE / 1024;
        List<Integer> iterPerThread = range(numberOfThreads, expected);
        Consumer<Integer> task = (Integer number) -> {
            for (int i = 0; i < number; i++) {
                counter.add(1);
            }
            counter.flush();
        };
        // when
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> futures = iterPerThread.stream()
                .map(number -> CompletableFuture.runAsync(() -> task.accept(number), executor))
                .toList();
            futures.forEach(CompletableFuture::join);
        }
        // then
        assertEquals(expected, counter.show());
    }
}
