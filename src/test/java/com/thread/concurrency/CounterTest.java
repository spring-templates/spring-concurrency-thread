package com.thread.concurrency;

import com.thread.concurrency.counter.BatchingCounter;
import com.thread.concurrency.counter.Counter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CounterTest {

    private final Counter counter;

    @Autowired
    // 필요한 Counter 구현체를 주입받는다.
    public CounterTest(BatchingCounter counter) {
        this.counter = counter;
    }

    @Test
    public void testAddInMultithreadedEnvironment() throws InterruptedException {
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
            // BatchingCounter는 4ms 이후 5ms마다 작업을 처리한다.
            // Batching 처리를 위해 20ms 대기
            // 하드웨어 성능에 따라 대기 시간을 조절해야 할 수도 있다.
            Thread.sleep(20);
        }

        int expectedValue = initialValue + nThreads * nAddsPerThread * valueToAdd;
        assertEquals(expectedValue, counter.show());
    }
}
