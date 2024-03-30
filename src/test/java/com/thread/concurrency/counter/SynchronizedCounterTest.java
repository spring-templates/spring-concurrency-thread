package com.thread.concurrency.counter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class SynchronizedCounterTest {

    private final SynchronizedCounter counter;
    private final int counteNumber = 1;
    private final int totalCount = 100;

    @Autowired
    public SynchronizedCounterTest(SynchronizedCounter counter) {
        this.counter = counter;
    }

    /**
     * 실행 완료까지 0.5s 정도 소요
     * @throws InterruptedException
     */
    @Test
    @DisplayName("synchronized로 스레드 안전한 카운터로 동시에 여러 더하기 수행하기. 활동성 문제 예상")
    public void 여러_더하기_수행_Executor() throws InterruptedException {
        int initalCount = counter.show();
        int numberOfThreads = totalCount;
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service.submit(() -> {
                counter.add(counteNumber);
                latch.countDown();
            });
        }
        latch.await();
        int finalCount = counter.show();

        Assertions.assertEquals(initalCount+totalCount*counteNumber, finalCount);
    }

    /**
     * 실행 완료까지 0.002s 소요
     */
    @Test
    @DisplayName("synchronized로 스레드 안전한 카운터로 동시에 여러 더하기 수행하기. 활동성 문제 예상")
    public void 여러_더하기_수행_CompletableFuture() {
        int initalCount = counter.show();
        List<CompletableFuture<Void>> tasks = new ArrayList<>();
        for(int i=0; i<totalCount; i++){
            tasks.add(CompletableFuture.runAsync(() -> counter.add(counteNumber)));
        }

        CompletableFuture<List<Void>> aggregate = CompletableFuture.completedFuture(new ArrayList<>());
        for (CompletableFuture<Void> future : tasks) {
            aggregate = aggregate.thenCompose(list -> {
                try {
                    list.add(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
                return CompletableFuture.completedFuture(list);
            });
        }
        aggregate.join(); // 전체 비동기 결과 집계
        int finalCount = counter.show();
        Assertions.assertEquals(initalCount+totalCount*counteNumber, finalCount);
    }
}
