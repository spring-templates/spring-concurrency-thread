package com.thread.concurrency.counter.producerCustomer;


public class CounterCustomer{
    private CounterBroker counterBroker;

    public CounterCustomer(CounterBroker counterBroker) {
        this.counterBroker = counterBroker;
    }

    public void consumEvent(){
        while(true){
            counterBroker.consumEvent();
        }
    }
}
