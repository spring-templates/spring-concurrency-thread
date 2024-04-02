package com.thread.concurrency.counter.producerCustomer;


public class CounterCustomer {
    private final CounterBroker counterBroker;
    private volatile boolean running;

    public CounterCustomer(CounterBroker counterBroker) {
        this.counterBroker = counterBroker;
        this.running = true;
    }
    public void stop() {
        running = false; // 스레드 종료를 위해 running 플래그를 false로 설정
    }
    public void consumEvent(){
        while(running){
            counterBroker.consumEvent();
        }
    }
}
