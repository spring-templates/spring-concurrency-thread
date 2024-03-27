package com.thread.concurrency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class SpringThreadConcurrencyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringThreadConcurrencyApplication.class, args);
    }
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor(); // Spring에서 사용하는 스레드를 제어한느 설정
        executor.setCorePoolSize(50); // thread-pool에 살아있는 thread의 최소 개수
        executor.setMaxPoolSize(50);  // thread-pool에서 사용할 수 있는 최대 개수
        executor.setQueueCapacity(500); //thread-pool에 최대 queue 크기
        executor.setThreadNamePrefix("AsyncApp-");
        executor.initialize();
        return executor;
    }
}
