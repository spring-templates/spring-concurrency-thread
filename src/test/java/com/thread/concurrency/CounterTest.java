package com.thread.concurrency;

import com.thread.concurrency.counter.AtomicCounter;
import com.thread.concurrency.counter.CompletableFutureCounter;
import com.thread.concurrency.counter.Counter;
import com.thread.concurrency.counter.SynchronizedCounter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@SpringBootTest
public class CounterTest {
    private final int valueToAdd = 1;
    private final int nAddsPerThread = 50000000;
    private final int nThreads = 9;

    public static Stream<Counter> counterProvider() {
        return Stream.of(new AtomicCounter(), new CompletableFutureCounter(), new SynchronizedCounter());
    }

    @ParameterizedTest
    @MethodSource("counterProvider")
    @DisplayName("스레드 안전한 카운터로 동시에 여러 더하기 수행하기.")
    public void 여러_더하기_수행_Executor(Counter counter) throws InterruptedException {
        LocalTime lt1 = LocalTime.now();
        int initalCount = counter.show();

        ExecutorService service = Executors.newFixedThreadPool(nThreads);
        CountDownLatch latch = new CountDownLatch(nThreads);
        for (int i = 0; i < nThreads; i++) {
            service.submit(() -> {
                for(int j=0; j<nAddsPerThread; j++){
                    counter.add(valueToAdd);
                }
                latch.countDown();
            });
        }
        latch.await();
        int finalCount = counter.show();
        LocalTime lt2 = LocalTime.now();
        long dif = Duration.between(lt1, lt2).getNano();
        System.out.println("여러_더하기_수행_Executor 테스트가 걸린 시간 : " + ((float)dif / 1000000) + "ms");
        Runtime.getRuntime().gc();
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("메모리 사용량 "+(double)usedMemory/1048576 + " MB");
        Assertions.assertEquals(initalCount + nThreads*nAddsPerThread*valueToAdd, finalCount);
    }
}
