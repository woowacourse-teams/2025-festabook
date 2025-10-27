package com.daedan.festabook.global.lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrencyTestHelper {

    private ConcurrencyTestHelper() {
    }

    public static void test(int requestCount, Runnable... requests) {
        validateRequests(requests);

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

    private static void validateRequests(Runnable[] requests) {
        if (requests.length == 0) {
            throw new IllegalArgumentException("실행할 api 인자는 최소 1개 이상이어야 합니다.");
        }
    }
}
