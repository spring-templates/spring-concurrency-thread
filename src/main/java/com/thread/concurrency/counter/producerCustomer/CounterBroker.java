package com.thread.concurrency.counter.producerCustomer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

public class CounterBroker {
    private BlockingQueue<Function<Integer, Integer>> queue = new LinkedBlockingQueue<>();
    private Integer count;
    public void addEvent(int value){
        try{
            queue.put((c) -> c + value); // 이 이벤트를 컨슈머가 처리할 당시 count와 value를 더한 값을 출력한다.
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    public void consumEvent(){
        try{
            // "value를 더한다"라는 이벤트는 현재 스레드만 가질 수 있다.
            count = queue.take().apply(count);
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
}
