package com.thread.concurrency.queueCounter;

import com.thread.concurrency.counter.queueCounter.CounterConsumer;
import com.thread.concurrency.counter.queueCounter.CounterProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.*;

public class QueueCounterTest {
    private final int valueToAdd = 1;
    private final int nAddsPerThread = 50000000;
    private final int nThreads = 9;
    private final long timeout = 1;
    private final TimeUnit unit = TimeUnit.SECONDS;

    @Test
    @DisplayName("멀티 프로듀서 싱글 컨슈머")
    public void 멀티_프로듀서_싱글_컨슈머() throws InterruptedException {
        BlockingQueue<Long> queue = new LinkedBlockingQueue<>(1000);
        CounterConsumer consumer = new CounterConsumer(queue);
        CounterProducer producer = new CounterProducer(queue);
        LocalTime lt1 = LocalTime.now();
        Long initalCount = consumer.show();
        ExecutorService producerService = Executors.newFixedThreadPool(nThreads);
        ExecutorService consumerService = Executors.newFixedThreadPool(nThreads);
        CountDownLatch producerLatch = new CountDownLatch(nThreads);
        CountDownLatch consumerLatch = new CountDownLatch(1);

        // 프로듀서 스레드 생성
        for (int i = 0; i < nThreads; i++) {
            producerService.submit(() -> {
                try {
                    for(int j=0; j<nAddsPerThread; j++){
                        producer.add(valueToAdd);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                producerLatch.countDown();
            });
        }

        // 컨슈머 스레드 생성
        consumerService.submit(() -> {
            try {
                consumer.consumeEvent(timeout, unit);
                consumerLatch.countDown();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        consumerLatch.await();
        System.out.println("컨슈머 작업 끝남");
        producerLatch.await();
        System.out.println("프로듀서 작업 끝남");

        Long finalCount = consumer.show();
        LocalTime lt2 = LocalTime.now();
        long dif = Duration.between(lt1, lt2).getNano();
        System.out.println("프로듀서_컨슈며_더하기_멀티_프로듀서_단일_컨슈머 테스트가 걸린 시간 : " + dif / 1000000 + "ms");
        Assertions.assertEquals(initalCount + nAddsPerThread*nThreads*valueToAdd, finalCount);
    }
    @Test
    @DisplayName("멀티 프로듀서 멀티 컨슈머")
    public void 멀티_프로듀서_멀티_컨슈머() throws InterruptedException {
        BlockingQueue<Long> queue = new LinkedBlockingQueue<>(1000);
        CounterConsumer consumer = new CounterConsumer(queue);
        CounterProducer producer = new CounterProducer(queue);
        LocalTime lt1 = LocalTime.now();
        Long initalCount = consumer.show();
        ExecutorService producerService = Executors.newFixedThreadPool(nThreads);
        ExecutorService consumerService = Executors.newFixedThreadPool(nThreads);
        CountDownLatch producerLatch = new CountDownLatch(nThreads);
        CountDownLatch consumerLatch = new CountDownLatch(nThreads);

        // 프로듀서 스레드 생성
        for (int i = 0; i < nThreads; i++) {
            producerService.submit(() -> {
                try {
                    for(int j=0; j<nAddsPerThread; j++){
                        producer.add(valueToAdd);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                producerLatch.countDown();
            });
        }
        // 컨슈머 스레드 생성
        for (int i = 0; i < nThreads; i++) {
            consumerService.submit(() -> {
                try {
                    consumer.consumeEvent(timeout, unit);
                    consumerLatch.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        consumerLatch.await();
        System.out.println("컨슈머 작업 끝남");
        producerLatch.await();
        System.out.println("프로듀서 작업 끝남");

        Long finalCount = consumer.show();
        LocalTime lt2 = LocalTime.now();
        long dif = Duration.between(lt1, lt2).getNano();
        System.out.println("프로듀서_컨슈며_더하기_멀티_프로듀서_단일_컨슈머 테스트가 걸린 시간 : " + dif / 1000000 + "ms");
        Assertions.assertEquals(initalCount + nAddsPerThread*nThreads*valueToAdd, finalCount);
    }
}
