package com.thread.concurrency.counter.producerCustomer;

import java.util.concurrent.BlockingQueue;
import java.util.function.Function;

public class CounterProducer {
    private final BlockingQueue<Long> queue;

    public CounterProducer(BlockingQueue<Long> queue) {
        this.queue = queue;
    }

    public void add(long value) throws InterruptedException {
        queue.put(value);
    }
}
