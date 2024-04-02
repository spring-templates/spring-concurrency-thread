package com.thread.concurrency.counter;

import com.thread.concurrency.counter.producerCustomer.CounterBroker;
import com.thread.concurrency.counter.producerCustomer.CounterCustomer;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueueCounterTest {
    private final int counteNumber = 1;
    private final int totalCount = 10000;
    private static final Logger logger = LoggerFactory.getLogger(SynchronizedCounterTest.class);
    @Test
    @DisplayName("producer consumer 패턴을 이용해서 더하기 이벤트 발생 스레드와 더하기 이벤트 처리 스레드를 분리하자")
    public void 프로듀서_컨슈며_더하기_멀티_프로듀서_멀티_컨슈머() throws InterruptedException {
        CounterBroker counterBroker = new CounterBroker();
        CounterCustomer customer = new CounterCustomer(counterBroker);
        CounterProducer producer = new CounterProducer(counterBroker);
        LocalTime lt1 = LocalTime.now();
        int initalCount = counterBroker.show();
        ExecutorService service = Executors.newFixedThreadPool(15);
        CountDownLatch latch = new CountDownLatch(totalCount);

        // CounterCustomer 스레드 생성 및 비동기로 처리 시작
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for(int i=0; i<3; i++){
            futureList.add(CompletableFuture.runAsync(customer::consumEvent));
        }
        // 프로듀서 스레드 생성
        for (int i = 0; i < totalCount; i++) {
            service.submit(() -> {
                producer.add(counteNumber);
                latch.countDown();
            });
        }
        latch.await();

        int finalCount = counterBroker.show();
        LocalTime lt2 = LocalTime.now();
        long dif = Duration.between(lt1, lt2).getNano();
        logger.info("프로듀서_컨슈며_더하기_멀티_프로듀서_단일_컨슈머 테스트가 걸린 시간 : " + dif / 1000 + "ms");
        Assertions.assertEquals(initalCount + totalCount*counteNumber, finalCount);
    }
}
