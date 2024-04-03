package com.thread.concurrency.counter;

import com.thread.concurrency.AsyncServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class SynchronizedCounterTest {

    private final int counteNumber = 1;
    private final int totalCount = 10000;
    private static final Logger logger = LoggerFactory.getLogger(SynchronizedCounterTest.class);

    /**
     * 실행 완료까지 871ms 정도 소요
     * @throws InterruptedException
     */
    @Test
    @DisplayName("synchronized로 스레드 안전한 카운터로 동시에 여러 더하기 수행하기. 활동성 문제 예상")
    public void 여러_더하기_수행_Executor() throws InterruptedException {
        SynchronizedCounter counter = new SynchronizedCounter();
        LocalTime lt1 = LocalTime.now();
        int initalCount = counter.show();
        int numberOfThreads = 15;
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
        logger.info("여러_더하기_수행_Executor 테스트가 걸린 시간 : "+dif/1000000+"ms");
        Assertions.assertEquals(initalCount+totalCount*counteNumber, finalCount);
    }

    /**
     * 실행 완료까지 1061ms 소요
     */
    @Test
    @DisplayName("synchronized로 스레드 안전한 카운터로 동시에 여러 더하기 수행하기. 활동성 문제 예상")
    public void 여러_더하기_수행_CompletableFuture() {
        SynchronizedCounter counter = new SynchronizedCounter();
        LocalTime lt1 = LocalTime.now();
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

        LocalTime lt2 = LocalTime.now();
        long dif = Duration.between(lt1, lt2).getNano();
        logger.info("여러_더하기_수행_CompletableFuture 테스트가 걸린 시간 : "+dif/1000000+"ms");
        Assertions.assertEquals(initalCount+totalCount*counteNumber, finalCount);
    }
}
