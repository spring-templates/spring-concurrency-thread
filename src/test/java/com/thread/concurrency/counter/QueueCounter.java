package com.thread.concurrency.counter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueCounter {
    private final int counteNumber = 1;
    private final int totalCount = 10000;
    private static final Logger logger = LoggerFactory.getLogger(SynchronizedCounterTest.class);

    @Test
    @DisplayName("producer consumer 패턴을 이용해서 더하기 이벤트 발생 스레드와 더하기 이벤트 처리 스레드를 분리하자")
    public void 프로듀서_컨슈며_더하기_멀티_프로듀서_단일_컨슈머(){

    }
}
