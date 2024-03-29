package com.thread.concurrency.logger;


import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DelayedLogger implements Logger {
    private final org.slf4j.Logger logger;

    public DelayedLogger() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void info(String message, Long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info(message);
    }
}
