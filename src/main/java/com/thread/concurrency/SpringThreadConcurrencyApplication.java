package com.thread.concurrency;

import com.thread.concurrency.counter.batch.BatchCounter;
import com.thread.concurrency.counter.batch.ConcurrentBatchingCounter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@SpringBootApplication
public class SpringThreadConcurrencyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringThreadConcurrencyApplication.class, args);

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage initialMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        long initialTime = System.currentTimeMillis();

        // Run the test
        int totalRequest = Integer.MAX_VALUE;
        conditionalMultiThreading(totalRequest);

        MemoryUsage finalMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        long finalTime = System.currentTimeMillis();
        long elapsedTime = finalTime - initialTime;
        long usedMemory = finalMemoryUsage.getUsed() - initialMemoryUsage.getUsed();

        // request with comma
        System.out.println("Total request: " + String.format("%,d", totalRequest));
        // seconds
        System.out.println("Elapsed time: " + elapsedTime / 1000 + " s");
        // megabytes
        System.out.println("Used memory: " + usedMemory / 1024 / 1024 + " MB");
    }

    private static void conditionalMultiThreading(int expected) {
        BatchCounter counter = new ConcurrentBatchingCounter();

        // given
        int numberOfThreads = 128;
        List<Integer> iterPerThread = range(numberOfThreads, expected);
        Consumer<Integer> task = (Integer number) -> {
            for (int i = 0; i < number; i++) {
                counter.add(1);
            }
            counter.flush();
        };
        // when
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> futures = iterPerThread.stream().map(number -> CompletableFuture.runAsync(() -> task.accept(number), executor)).toList();
            futures.forEach(CompletableFuture::join);
        }
        // then
        assert expected == counter.show();
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

}
