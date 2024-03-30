package com.thread.concurrency.counter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootTest
public class BasicCounterTest {

    private final BasicCounter basicCounter;
    private final int counteNumber = 1;
    private final int totalCount = 100;

    @Autowired
    public BasicCounterTest(BasicCounter basicCounter) {
        this.basicCounter = basicCounter;
    }

    @Test
    @DisplayName("스레드 안전하지 않는 카운터로 동시에 여러 더하기 수행하기. 실패 예상")
    public void 여러_더하기_수행(){
        int initalCount = basicCounter.show();

       for(int i=0; i<totalCount; i++){
           CompletableFuture.runAsync(() -> {
               basicCounter.add(counteNumber);
           });
       }
        int finalCount = basicCounter.show();
        Assertions.assertNotEquals(initalCount+totalCount*counteNumber, finalCount);
    }

    @Test
    @DisplayName("synchronized로 스레드 안전한 카운터로 동시에 여러 더하기 수행하기. 활동성 문제 예상")
    public void 여러_더하기_수행_CompletableFuture() {
        int initalCount = basicCounter.show();
        List<CompletableFuture<Void>> tasks = new ArrayList<>();
        for(int i=0; i<totalCount; i++){
            tasks.add(CompletableFuture.runAsync(() -> basicCounter.add(counteNumber)));
        }

        CompletableFuture<List<Void>> aggregate = CompletableFuture.completedFuture(new ArrayList<>());
        for (CompletableFuture<Void> future : tasks) {
            aggregate = aggregate.thenCompose(list -> {
                try {
                    list.add(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
                return CompletableFuture.completedFuture(list);
            });
        }
        aggregate.join(); // 전체 비동기 결과 집계
        int finalCount = basicCounter.show();
        Assertions.assertEquals(initalCount+totalCount*counteNumber, finalCount);
    }
}
