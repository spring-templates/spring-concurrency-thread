package com.thread.concurrency.async.controller;

import com.thread.concurrency.async.service.AsyncService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
public class AsyncController {
    private final AsyncService asyncService;

    public AsyncController(AsyncService asyncService) {
        this.asyncService = asyncService;
    }

    @Async
    public CompletableFuture<String> calculateRunTime(int cnt, int waitTime) throws InterruptedException {
        LocalTime lt1 = LocalTime.now();
        List<CompletableFuture<String>> hellos = new ArrayList<>();
        for (int i = 0; i < cnt; i++) {
            hellos.add(asyncService.voidParamStringReturn(waitTime, i + "번째 메세지"));
        }
        // 모든 비동기 호출이 완료될 때까지 대기하고 결과를 리스트에 넣기
        List<String> results = hellos.stream().map(CompletableFuture::join)
            .toList();
        LocalTime lt2 = LocalTime.now();
        long dif = Duration.between(lt1, lt2).toMillis();
        return CompletableFuture.completedFuture(dif + "가 걸렸습니다.");
    }
}
