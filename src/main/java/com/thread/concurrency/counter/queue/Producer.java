package com.thread.concurrency.counter.queue;

public interface Producer {
    void add(long value) throws InterruptedException;
}
