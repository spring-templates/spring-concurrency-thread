package com.thread.concurrency.counter.queue;

import java.util.concurrent.TimeUnit;

public interface Consumer {
    void consumeEvent(long timeout, TimeUnit unit) throws InterruptedException;

    Long show();
}
