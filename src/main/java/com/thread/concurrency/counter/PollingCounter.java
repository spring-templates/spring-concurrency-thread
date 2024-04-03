package com.thread.concurrency.counter;

import org.springframework.stereotype.Component;

@Component
// use technique spin-lock(busy waiting)
// this approach can lead to high CPU usage if the lock is heavily contended
public class PollingCounter implements Counter {
    private static int count = 100;
    private static volatile boolean lock = false;

    private static void doAdd(int value) {
        count += value;
    }

    @Override
    public void add(int value) {
        while (true) {
            if (!lock) {
                synchronized (PollingCounter.class) {
                    lock = true;
                    doAdd(value);
                    lock = false;
                    break;
                }
            }
        }
    }

    @Override
    public int show() {
        return count;
    }
}
