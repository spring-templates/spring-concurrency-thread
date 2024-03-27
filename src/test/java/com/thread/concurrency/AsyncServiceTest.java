package com.thread.concurrency;

import com.thread.concurrency.async.service.AsyncService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class AsyncServiceTest {
    @Autowired
    private AsyncService asyncService;
    @Test
    @DisplayName("입력은 void 출력은 String인 비동기 함수 단일 호출")
    public void testGetString() throws ExecutionException, InterruptedException {
        CompletableFuture<String> helloWorld = asyncService.voidParamStringReturn(1000);
        Assertions.assertEquals("hello world",helloWorld.get());
    }
    @Test
    @DisplayName("입력은 void 출력은 String인 비동기 함수 단일 호출 타임아웃 발생.")
    public void testGetStringTimeOutIsThisAsync() {
        // voidParamStringReturn가 비동기 메서드인지 의문이 생김.
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return asyncService.voidParamStringReturn(4000).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        long timeOutValue = 1;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        // 1초가 지난 후 타임아웃 발생
        Assertions.assertThrows(ExecutionException.class, () -> completableFuture.orTimeout(timeOutValue,timeUnit).get());
    }

    @Test
    @DisplayName("입력은 void 출력은 String인 비동기 함수 복수 호출 그리고 결과 조합")
    public void testMultiGetString() {
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) { // 동기라면 10초가 걸리고 비동기라면 0.01초가 걸릴 것이다.
            futures.add(asyncService.voidParamStringReturn(10));
        }
        CompletableFuture<List<String>> aggregate = CompletableFuture.completedFuture(new ArrayList<>());
        for (CompletableFuture<String> future : futures) {
            aggregate = aggregate.thenCompose(list -> {
                list.add(String.valueOf(future));
                return CompletableFuture.completedFuture(list);
            });
        }
        final List<String> results = aggregate.join();
        for (int i = 0; i < 1000; i++) {
            System.out.println("Finished " + results.get(i));
        }

    }
}
