package com.thread.concurrency.counter.producerCustomer;

public class CounterProducer {
    private CounterBroker counterBroker;

    public CounterProducer(CounterBroker counterBroker) {
        this.counterBroker = counterBroker;
    }

    public void add(int value){
        counterBroker.addEvent(value);
    }
}
