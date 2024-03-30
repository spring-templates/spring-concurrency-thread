package com.thread.concurrency.counter;

import net.jcip.annotations.GuardedBy;
import org.springframework.stereotype.Component;

@Component
public class SynchronizedCounter implements Counter{

    @GuardedBy("this")
    private int counter = 100;

    @Override
    public synchronized void add(int value) {
        counter += value;
    }

    @Override
    public synchronized int show() {
        return counter;
    }
}
