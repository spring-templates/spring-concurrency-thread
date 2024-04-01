package com.thread.concurrency.counter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;

@SpringBootTest
public class CounterTest {

    public static Stream<Counter> counterProvider() {
        return Stream.of(new BatchingCounter(), new LockCounter(), new PollingCounter(), new BasicCounter());
    }

    private static void assertThen(Counter counter, int expectedValue, int actualValue) {
        System.out.println("Expected value: " + expectedValue);
        System.out.println("Actual value: " + actualValue);
        if (counter instanceof BasicCounter) {
            System.out.println("BasicCounter is not thread-safe");
            Assertions.assertNotEquals(expectedValue, actualValue);
        } else {
            System.out.println("Counter is thread-safe");
            Assertions.assertEquals(expectedValue, actualValue);
        }
    }

    @ParameterizedTest
    @MethodSource("counterProvider")
    public void stressTest(Counter counter) throws InterruptedException {
        int initialValue = counter.show();
        int nThreads = 100;
        int nAddsPerThread = 1000;
        int valueToAdd = 1;
        int expectedValue = initialValue + nThreads * nAddsPerThread * valueToAdd;


        // define runnable job
        CountDownLatch latch = new CountDownLatch(nThreads);
        Runnable job = () -> {
            try {
                latch.countDown(); // decrease the count
                latch.await(); // wait until the count reaches 0
                for (int i = 0; i < nAddsPerThread; i++) {
                    counter.add(valueToAdd);
                }
            } catch (InterruptedException ignored) {
            }
        };

        // start nThreads threads
        for (int i = 0; i < nThreads; i++) {
            Thread.ofVirtual().start(job);
        }

        sleep(300); // wait for all threads to finish

        assertThen(counter, expectedValue, counter.show());
    }
}
