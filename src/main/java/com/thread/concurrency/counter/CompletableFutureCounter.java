package com.thread.concurrency.counter;

import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class CompletableFutureCounter implements Counter{

    private CompletableFuture<Integer> counter = new CompletableFuture<>();

    @Override
    public void add(int value) {
        counter = counter.thenApply((c) -> c + value);
    }

    @Override
    public int show() {
        try {
            return counter.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
