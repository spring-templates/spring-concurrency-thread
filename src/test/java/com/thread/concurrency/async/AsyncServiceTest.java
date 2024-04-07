package com.thread.concurrency.async;

import com.thread.concurrency.async.service.AsyncService;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class AsyncServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(AsyncServiceTest.class);
    @Autowired
    private AsyncService asyncService;

    @Test
    @DisplayName("입력은 void 출력은 String인 비동기 함수 단일 호출")
    public void testGetString() throws ExecutionException, InterruptedException {
        CompletableFuture<String> helloWorld = asyncService.voidParamStringReturn(1000,  "기본 메세지");
        Assertions.assertEquals("기본 메세지",helloWorld.get());
    }

    @Test
    @DisplayName("입력은 void 출력은 String인 비동기 함수 다중 호출")
    public void testGetMultiString() throws InterruptedException {
        List<CompletableFuture<String>> hellos = new ArrayList<>();
        for(int i=0; i<100; i++){
            hellos.add(asyncService.voidParamStringReturn(1000,i+"번째 메세지"));
        }
        // 모든 비동기 호출이 완료될 때까지 대기하고 결과를 리스트에 넣기
        List<String> results = hellos.stream().map(CompletableFuture::join)
                .toList();
        results.forEach(logger::info);
    }

    @Test
    @DisplayName("입력은 void 출력은 String인 비동기 함수 단일 호출 타임아웃 발생.")
    public void testGetStringTimeOutIsThisAsync() throws InterruptedException {
        // voidParamStringReturn가 비동기 메서드인지 의문이 생김.
        CompletableFuture<String> completableFuture = asyncService.voidParamStringReturn(4000, "타임아웃 발생 안 함!");
        long timeOutValue = 1;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        // 1초가 지난 후 타임아웃 발생
        Assertions.assertThrows(ExecutionException.class, () -> completableFuture.orTimeout(timeOutValue,timeUnit).get());
    }
}
