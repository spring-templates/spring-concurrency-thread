package com.thread.concurrency.counter.producerCustomer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;

public class CounterConsumer {
    private final BlockingQueue<Long> queue;
    private final AtomicLong count = new AtomicLong(0); // 스레드 안전성은 synchronized에게 맞기기 때문에 int로 변경.

    public CounterConsumer(BlockingQueue<Long> queue) {
        this.queue = queue;
    }

    public void consumeEvent() throws InterruptedException {

        while (!queue.isEmpty()) {
            System.out.println(Thread.currentThread().getName()+"은 현재 큐 사이즈 : "+queue.size());
            Long value = queue.take();
//            count.addAndGet(value);
            System.out.println("결과 카운트 : "+count.addAndGet(value));
        }
    }
    public Long show(){
        while(true){
            if(queue.isEmpty()){
                return count.get();
            }
        }
    }
}
