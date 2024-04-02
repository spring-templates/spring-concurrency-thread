package com.thread.concurrency.counter.producerCustomer;

import java.util.concurrent.BlockingQueue;
import java.util.function.Function;

public class CounterProducer {
    private final BlockingQueue<Function<Integer, Integer>> queue;

    public CounterProducer(BlockingQueue<Function<Integer, Integer>> queue) {
        this.queue = queue;
    }

    public void add(int value) throws InterruptedException {
        queue.put((c) -> c + value);
    }
}
