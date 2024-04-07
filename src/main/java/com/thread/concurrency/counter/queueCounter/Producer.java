package com.thread.concurrency.counter.queueCounter;

public interface Producer {
    void add(long value) throws InterruptedException;
}
