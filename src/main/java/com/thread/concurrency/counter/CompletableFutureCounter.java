package com.thread.concurrency.counter;

import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CompletableFutureCounter implements Counter{

    private CompletableFuture<Integer> counter = new CompletableFuture<>();

    @Override
    public void add(int value) {
        // 연산이 진행 중이라면 기다렸다가 thenApply
        // 카운트에 값 저장
        counter = counter.thenApply((c) -> c + value);
    }

    @Override
    public int show() {
        try {
            // 카운트에 대한 연산이 실행 중이라면 기다렸다가 가져오기
            return counter.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
