package com.thread.concurrency.counter.queueCounter;

import java.util.concurrent.BlockingQueue;

public class CounterProducer {
    private final BlockingQueue<Long> queue;

    public CounterProducer(BlockingQueue<Long> queue) {
        this.queue = queue;
    }

    public void add(long value) throws InterruptedException {
        queue.put(value);
    }
}
