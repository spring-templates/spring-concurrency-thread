package com.thread.concurrency.counter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class CompletableFutureCounterTest {
    private final int counteNumber = 1;
    private final int totalCount = 10000;
    private static final Logger logger = LoggerFactory.getLogger(CompletableFutureCounterTest.class);

    @Test
    public void 여러_더하기_수행_Compltable() throws InterruptedException {
        SynchronizedCounter counter = new SynchronizedCounter();
        LocalTime lt1 = LocalTime.now();

        int initalCount = counter.show();
        ExecutorService service = Executors.newFixedThreadPool(15);
        CountDownLatch latch = new CountDownLatch(totalCount);
        for (int i = 0; i < totalCount; i++) {
            service.submit(() -> {
                counter.add(counteNumber);
                latch.countDown();
            });
        }
        latch.await();
        int finalCount = counter.show();

        LocalTime lt2 = LocalTime.now();
        long dif = Duration.between(lt1, lt2).getNano();
        logger.info("여러_더하기_수행_Compltable 테스트가 걸린 시간 : "+dif/1000000+"ms");
        Assertions.assertEquals(initalCount+totalCount*counteNumber, finalCount);
    }
}
