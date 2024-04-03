package com.thread.concurrency.counter;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class BatchingCounter implements Counter {
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private static final ConcurrentLinkedQueue<Integer> jobQueue = new ConcurrentLinkedQueue<>();
    private static volatile int count = 100;

    public BatchingCounter() {
        Runnable runnableTask = () -> {
            while (!jobQueue.isEmpty()) {
                synchronized (this) {
                    var value = jobQueue.poll();
                    count += value == null ? 0 : value;
                }
            }
        };
        // context switching을 최소화하는 최소한의 시간마다 실행하여 성능 향상
        scheduledExecutorService.scheduleAtFixedRate(runnableTask, 4, 5, TimeUnit.MILLISECONDS);
    }

    @Override
    public void add(int value) {
        jobQueue.add(value);
    }

    @Override
    public int show() {
        return count;
    }
}
