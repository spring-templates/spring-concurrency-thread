package com.thread.concurrency.counter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.concurrent.CompletableFuture;

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
}
