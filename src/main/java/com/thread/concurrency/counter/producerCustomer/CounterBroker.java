package com.thread.concurrency.counter.producerCustomer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

public class CounterBroker {
    // 100개의 이벤트를 저장할 수 있다.
    private BlockingQueue<Function<Integer, Integer>> queue = new LinkedBlockingQueue<>(100);
    private AtomicInteger count = new AtomicInteger(100); // AtomicInteger로 변경
    public void addEvent(int value){
        try{
            // 이 이벤트를 컨슈머가 처리할 당시 count와 value를 더한 값을 출력한다.
            // 만약 4초 동안 프로듀서가 요소를 넣지 못하면 timeout이 발생한다.
            queue.offer((c) -> c + value, 4, TimeUnit.SECONDS);
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    public void consumEvent(){
        try{
            // "value를 더한다"라는 이벤트는 현재 스레드만 가질 수 있다.
            // AtomicInteger의 updateAndGet 메서드를 사용하여 원자적으로 값을 업데이트
            Function<Integer, Integer> event = queue.take();
            IntUnaryOperator operator = event::apply;
            count.updateAndGet(operator);
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    public int show(){
        return count.get(); // AtomicInteger의 get 메서드를 사용하여 값을 가져옴
    }
}
