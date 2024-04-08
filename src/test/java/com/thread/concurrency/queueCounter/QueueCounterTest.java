package com.thread.concurrency.queueCounter;

import com.thread.concurrency.counter.queueCounter.Consumer;
import com.thread.concurrency.counter.queueCounter.CounterConsumer;
import com.thread.concurrency.counter.queueCounter.CounterProducer;
import com.thread.concurrency.counter.queueCounter.Producer;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.concurrent.*;

public class QueueCounterTest {
    private static final int valueToAdd = 1;
    private static final int nAddsPerThread = 1000000;
    private static final int producerNThreads = 9;
    private static final int consumerNThreads = 9;
    private Consumer consumer;
    private Producer producer;
    private ExecutorService consumerService;
    private ExecutorService producerService;

    @BeforeEach
    public void setup() {
        int queueCapacity = 1000;
        BlockingQueue<Long> queue = new LinkedBlockingQueue<>(queueCapacity);
        consumer = new CounterConsumer(queue);
        producer = new CounterProducer(queue);
        producerService = Executors.newFixedThreadPool(producerNThreads);
        consumerService = Executors.newFixedThreadPool(consumerNThreads);
    }

    @AfterEach
    public void cleanup() {
        producerService.shutdown();
        consumerService.shutdown();
    }

    @Test
    @SuppressWarnings("SpellCheckingInspection")
    @DisplayName("멀티 프로듀서 싱글 컨슈머")
    public void multiProducerSingleConsumer() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            runTest(1);
        });
    }

    @Test
    @SuppressWarnings("SpellCheckingInspection")
    @DisplayName("멀티 프로듀서 멀티 컨슈머")
    public void multiProducerMultiConsumer() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            runTest(consumerNThreads);
        });
    }

    private void runTest(int consumerCount) throws InterruptedException {
        Long initialCount = consumer.show();
        CountDownLatch producerLatch = new CountDownLatch(producerNThreads);
        CountDownLatch consumerLatch = new CountDownLatch(consumerCount);

        createProducerThreads(producerLatch);
        createConsumerThreads(consumerLatch, consumerCount);

        consumerLatch.await();
        producerLatch.await();

        Long finalCount = consumer.show();
        Assertions.assertEquals(initialCount + nAddsPerThread * producerNThreads * valueToAdd, finalCount);
    }

    private void createProducerThreads(CountDownLatch producerLatch) {
        for (int i = 0; i < producerNThreads; i++) {
            producerService.submit(() -> {
                try {
                    for (int j = 0; j < nAddsPerThread; j++) {
                        producer.add(valueToAdd);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                producerLatch.countDown();
            });
        }
    }

    private void createConsumerThreads(CountDownLatch consumerLatch, int consumerCount) {
        for (int i = 0; i < consumerCount; i++) {
            consumerService.submit(() -> {
                try {
                    consumer.consumeEvent(1, TimeUnit.SECONDS);
                    consumerLatch.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
