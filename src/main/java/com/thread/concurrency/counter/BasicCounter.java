package com.thread.concurrency.counter;

import org.springframework.stereotype.Component;

@Component
public class BasicCounter implements Counter{
    private static int count = 100;

    @Override
    public void add(int value) {
        count += value;
    }

    @Override
    public int show() {
        return count;
    }
}
