package com.thread.concurrency.counter.queueCounter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class CounterConsumer {
    private final BlockingQueue<Long> queue;
    private final AtomicLong count = new AtomicLong(0);

    public CounterConsumer(BlockingQueue<Long> queue) {
        this.queue = queue;
    }

    public void consumeEvent(long timeout, TimeUnit unit) throws InterruptedException {
        while (true) {
//            System.out.println(Thread.currentThread().getName()+"은 현재 큐 사이즈 : "+queue.size());
            Long value = queue.poll(timeout, unit);
            if(value == null){
//                System.out.println(Thread.currentThread().getName()+" 끝났으!!");
                break;
            }
            count.addAndGet(value);
//            System.out.println("결s과 카운트 : "+count.addAndGet(value));
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
