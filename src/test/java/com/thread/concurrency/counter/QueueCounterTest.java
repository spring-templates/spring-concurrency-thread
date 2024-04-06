package com.thread.concurrency.counter;

import com.thread.concurrency.counter.producerCustomer.CounterConsumer;
import com.thread.concurrency.counter.producerCustomer.CounterProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

public class QueueCounterTest {
    private final int counteNumber = 1;
    private final int totalCount = 50000000; // Integer.MAX_VALUE;
    private final int nThread = 15;
    private static final Logger logger = LoggerFactory.getLogger(SynchronizedCounterTest.class);
    @Test
    @DisplayName("producer consumer 패턴을 이용해서 더하기 이벤트 발생 스레드와 더하기 이벤트 처리 스레드를 분리하자")
    public void 프로듀서_컨슈며_더하기_멀티_프로듀서_멀티_컨슈머() throws InterruptedException {
        BlockingQueue<Long> queue = new LinkedBlockingQueue<>(1000);
        CounterConsumer consumer = new CounterConsumer(queue);
        CounterProducer producer = new CounterProducer(queue);
        LocalTime lt1 = LocalTime.now();
        Long initalCount = consumer.show();
        ExecutorService service = Executors.newFixedThreadPool(nThread);
        CountDownLatch latch = new CountDownLatch(totalCount);

        // 프로듀서 스레드 생성
        for (int i = 0; i < nThread; i++) {
            service.submit(() -> {
                try {
                    for(int j=0; j<totalCount/nThread; j++){
                        producer.add(counteNumber);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                latch.countDown();
            });
        }
        // CounterCustomer 스레드 생성 및 비동기로 처리 시작
        for(int i=0; i<nThread; i++){
            CompletableFuture.runAsync(()->{
                try{
                    consumer.consumeEvent();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        latch.await();
//        Long finalCount = consumer.show();
        LocalTime lt2 = LocalTime.now();
        long dif = Duration.between(lt1, lt2).getNano();
        logger.info("프로듀서_컨슈며_더하기_멀티_프로듀서_단일_컨슈머 테스트가 걸린 시간 : " + dif / 1000000 + "ms");
//        Assertions.assertEquals(initalCount + totalCount*counteNumber, finalCount);
    }

    @Test
    @DisplayName("synchronized로 스레드 안전한 카운터로 동시에 여러 더하기 수행하기. 활동성 문제 예상")
    public void 여러_더하기_수행_CompletableFuture() throws InterruptedException {
        BlockingQueue<Long> queue = new LinkedBlockingQueue<>(1000);
        CounterConsumer consumer = new CounterConsumer(queue);
        CounterProducer producer = new CounterProducer(queue);
        LocalTime lt1 = LocalTime.now();
        Long initalCount = consumer.show();
        ExecutorService service = Executors.newFixedThreadPool(nThread);
        CountDownLatch latch = new CountDownLatch(nThread);

        // 프로듀서 스레드 생성
        for (int i = 0; i < nThread; i++) {
            service.submit(() -> {
                try {
                    for(int j=0; j<totalCount/nThread; j++){
                        producer.add(counteNumber);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                latch.countDown();
            });
        }
        // CounterCustomer 스레드 생성 및 비동기로 처리 시작
        List<CompletableFuture<Void>> tasks = new ArrayList<>();
        for(int i=0; i<3; i++){
            tasks.add(CompletableFuture.runAsync(()->{
                try{
                    consumer.consumeEvent();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
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
        System.out.println("컨슈머 작업 끝남");
        latch.await();
        System.out.println("프로듀서 작업 끝남");
        Long finalCount = consumer.show();
        LocalTime lt2 = LocalTime.now();
        long dif = Duration.between(lt1, lt2).getNano();
        logger.info("프로듀서_컨슈며_더하기_멀티_프로듀서_단일_컨슈머 테스트가 걸린 시간 : " + dif / 1000000 + "ms");
        Assertions.assertEquals(initalCount + (totalCount/nThread)*nThread*counteNumber, finalCount);
    }
}
