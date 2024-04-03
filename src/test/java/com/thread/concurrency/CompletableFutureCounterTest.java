package com.thread.concurrency;

import com.thread.concurrency.counter.CompletableFutureCounter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class CompletableFutureCounterTest {

    private final int counteNumber = 1;
    private final int totalCount = 5000;
    private final int maxThreadNumber = 15;
    private static final Logger logger = LoggerFactory.getLogger(CompletableFutureCounterTest.class);

    @Autowired
    CompletableFutureCounter counter;
    @Test
    @DisplayName("CompletableFuture로 스레드 안전한 카운터로 동시에 여러 더하기 수행하기.")
    public void 여러_더하기_수행_Executor() throws InterruptedException {
        LocalTime lt1 = LocalTime.now();
        int initalCount = counter.show();

        ExecutorService service = Executors.newFixedThreadPool(maxThreadNumber);
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
        logger.info("여러_더하기_수행_Executor 테스트가 걸린 시간 : " + ((float)dif / 1000000) + "ms");
        Assertions.assertEquals(initalCount + totalCount * counteNumber, finalCount);
    }
}
