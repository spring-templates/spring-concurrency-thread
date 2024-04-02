package com.thread.concurrency.counter.producerCustomer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class CounterBroker {
    // 최대 1000개의 이벤트를 저장할 수 있다.
    private final BlockingQueue<Function<Integer, Integer>> queue = new LinkedBlockingQueue<>(1000);

//    public void addEvent(int value){
//        try{
//            // 이 이벤트를 컨슈머가 처리할 당시 count와 value를 더한 값을 출력한다.
//            queue.put((c) -> c + value);
//        }
//        catch(InterruptedException e){
//            Thread.currentThread().interrupt();
//        }
//    }
//    public Function<Integer, Integer> take(){
//        try {
//            return queue.take();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
