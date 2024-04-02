package com.thread.concurrency.counter.producerCustomer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

public class CounterCustomer {
    private final BlockingQueue<Function<Integer, Integer>> queue;
    private AtomicInteger count = new AtomicInteger(100); // 스레드 안전성은 synchronized에게 맞기기 때문에 int로 변경.

    public CounterCustomer(BlockingQueue<Function<Integer, Integer>> queue) {
        this.queue = queue;
    }

    public void consumEvent() throws InterruptedException {
        while(!queue.isEmpty()){
            Function<Integer,Integer> event = queue.take();
            IntUnaryOperator operator = event::apply;
            synchronized (this){
                System.out.println(count.updateAndGet(operator));
            }
        }
    }
    public int show(){ // 큐가 비어지는 마지막 순간에 if문이 true가 되어 count를 출력해버린다...
        while(true){
            if(queue.isEmpty()){
                return count.get();
            }
        }
    }
}
