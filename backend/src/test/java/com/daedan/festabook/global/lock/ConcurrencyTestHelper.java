package com.daedan.festabook.global.lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrencyTestHelper {

    private ConcurrencyTestHelper() {
    }

    public static void test(int requestCount, Runnable... requests) {
        try (ExecutorService threadPool = Executors.newFixedThreadPool(requestCount)) {
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch endLatch = new CountDownLatch(requestCount);

            for (int i = 0; i < requestCount; i++) {
                int currentCount = i % requests.length;
                threadPool.submit(() -> {
                    try {
                        startLatch.await();
                        requests[currentCount].run();
                    } catch (InterruptedException ignore) {
                    } finally {
                        endLatch.countDown();
                    }
                });
            }

            startLatch.countDown();

            try {
                endLatch.await();
            } catch (InterruptedException ignore) {
            }
        }
    }
}
