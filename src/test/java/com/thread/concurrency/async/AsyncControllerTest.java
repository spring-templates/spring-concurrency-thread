package com.thread.concurrency.async;

import com.thread.concurrency.async.controller.AsyncController;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
public class AsyncControllerTest {
    private static final Logger logger = LoggerFactory.getLogger(AsyncServiceTest.class);

    @Autowired
    private AsyncController asyncController;

    @Test
    public void invokeMultiAsyncMethod() throws InterruptedException {
        List<CompletableFuture<String>> hellos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            hellos.add(asyncController.calculateRunTime(10, 1000));
        }
        // 모든 비동기 호출이 완료될 때까지 대기하고 결과를 리스트에 넣기
        List<String> results = hellos.stream().map(CompletableFuture::join)
            .toList();
        results.forEach(logger::info);
    }
}
