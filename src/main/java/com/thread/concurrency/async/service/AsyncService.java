package com.thread.concurrency.async.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
public class AsyncService {
    @Async
    public CompletableFuture<String> voidParamStringReturn (long waitTime, String message) throws InterruptedException{
        System.out.println("비동기적으로 실행 - "+
            Thread.currentThread().getName());
        Thread.sleep(waitTime);
        return CompletableFuture.completedFuture(message);
    }
}
