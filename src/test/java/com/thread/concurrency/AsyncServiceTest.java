package com.thread.concurrency;

import com.thread.concurrency.async.service.AsyncService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class AsyncServiceTest {
    private AsyncService asyncService;
    @BeforeEach
    void initUseCase() { // spring container를 사용하지 않고 순수 클래스를 사용.
        asyncService = new AsyncService();
    }
    @Test
    @DisplayName("입력은 void 출력은 String인 비동기 함수 단일 호출")
    public void testGetString() throws ExecutionException, InterruptedException {
        CompletableFuture<String> helloWorld = asyncService.voidParamStringReturn();
        Assertions.assertEquals("hello world",helloWorld.get());
    }

//    @Test
//    @DisplayName("입력은 void 출력은 String인 비동기 함수 복수 호출 그리고 결과 조합")
//    public void testMultiGetString() throws ExecutionException, InterruptedException {
//        List<CompletableFuture<String>> completableFutures = new ArrayList<>();
//
//        for (int j = 0; j <= 23; j++) {
//            completableFutures.add(asyncService.voidParamStringReturn()));
//        }
//        CompletableFuture.allOf(price1,price2,price3).join();
//        Assertions.assertEquals("hello world !!hello world !!", combin);
//    }
}
