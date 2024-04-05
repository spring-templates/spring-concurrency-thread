package com.thread.concurrency.counter;

import com.thread.concurrency.SpringThreadConcurrencyApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class SpringThreadConcurrencyApplicationTests {
    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> SpringThreadConcurrencyApplication.main(new String[]{}));
    }
}
