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
    private final int totalCount = Integer.MAX_VALUE;
    private final int nThread = 15;
    private static final Logger logger = LoggerFactory.getLogger(SynchronizedCounterTest.class);

    @Test
    @DisplayName("멀티 프로듀서 싱글 컨슈머")
    public void 멀티_프로듀서_싱글_컨슈머() throws InterruptedException {
        BlockingQueue<Long> queue = new LinkedBlockingQueue<>(1000);
        CounterConsumer consumer = new CounterConsumer(queue);
        CounterProducer producer = new CounterProducer(queue);
        LocalTime lt1 = LocalTime.now();
        Long initalCount = consumer.show();
        ExecutorService service = Executors.newFixedThreadPool(nThread);
        CountDownLatch latch = new CountDownLatch(nThread);

        // 프로듀서 스레드 생성
        for (int i = 0; i < nThread; i++) {
//            int finalI = i;
            service.submit(() -> {
                try {
                    for(int j=0; j<(totalCount/nThread); j++){
//                        System.out.println(Thread.currentThread().getName()+"은 작업 추가 "+ finalI +" "+j);
                        producer.add(counteNumber);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                latch.countDown();
            });
        }
        // CounterCustomer 스레드 생성 및 비동기로 처리 시작
        CompletableFuture<Void> task = CompletableFuture.runAsync(()->{
            try{
                consumer.consumeEvent(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            task.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        System.out.println("컨슈머 작업 끝남");
        latch.await();
        System.out.println("프로듀서 작업 끝남");

        Long finalCount = consumer.show();
        LocalTime lt2 = LocalTime.now();
        long dif = Duration.between(lt1, lt2).getNano();
        logger.info("프로듀서_컨슈며_더하기_멀티_프로듀서_단일_컨슈머 테스트가 걸린 시간 : " + dif / 1000000 + "ms");
        Assertions.assertEquals(initalCount + (totalCount/nThread)*nThread*counteNumber, finalCount);
    }
    @Test
    @DisplayName("멀티 프로듀서 멀티 컨슈머")
    public void 멀티_프로듀서_멀티_컨슈머() throws InterruptedException {
        BlockingQueue<Long> queue = new LinkedBlockingQueue<>(1000);
        CounterConsumer consumer = new CounterConsumer(queue);
        CounterProducer producer = new CounterProducer(queue);
        LocalTime lt1 = LocalTime.now();
        Long initalCount = consumer.show();
        ExecutorService service = Executors.newFixedThreadPool(nThread);
        CountDownLatch latch = new CountDownLatch(nThread);

        for (int i = 0; i < nThread; i++) {
            service.submit(() -> {
                try {
                    for(int j=0; j<(totalCount/nThread); j++){
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
            tasks.add( CompletableFuture.runAsync(()->{
                try{
                    consumer.consumeEvent(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
        }
        tasks.forEach((t) -> {
            try {
//                System.out.println("적당히 돌아가는거 확인합닛다.");
                t.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
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
