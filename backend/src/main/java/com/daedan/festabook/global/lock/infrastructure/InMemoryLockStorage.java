package com.daedan.festabook.global.lock.infrastructure;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.global.lock.LockStorage;
import com.daedan.festabook.global.logging.Loggable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Loggable
@Component
public class InMemoryLockStorage implements LockStorage {

    private final ConcurrentHashMap<String, Lock> locks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void tryLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit) {
        validateEmptyKey(key);
        long deadline = System.nanoTime() + timeUnit.toNanos(waitTime);
        long currentThreadId = Thread.currentThread().getId();

        Lock emptyLock = new Lock();
        while (true) {
            validateLockTimeOut(deadline);
            Lock existing = locks.putIfAbsent(key, emptyLock);
            if (existing == null) {
                emptyLock.registerOwnerThreadId(currentThreadId);
                emptyLock.registerLeaseTimeOutSchedule(
                        scheduler.schedule(leaseTimeOutSchedule(key, emptyLock), leaseTime, timeUnit)
                );
                return;
            }

            synchronized (existing) {
                try {
                    long nanosLeft = deadline - System.nanoTime();
                    long millisPart = calculateMillisPart(nanosLeft);
                    int nanosPart = calculateNanosPart(nanosLeft, millisPart);
                    existing.wait(millisPart, nanosPart);
                } catch (InterruptedException ignore) {
                }
            }
        }
    }

    @Override
    public void unlock(String key) {
        validateEmptyKey(key);
        Lock lock = locks.get(key);
        validateNotExistsLock(lock);
        long currentThreadId = Thread.currentThread().getId();
        validateLockOwner(lock, currentThreadId);

        synchronized (lock) {
            lock.cancelIfExistsLeaseTimeOutSchedule();
            locks.remove(key, lock);
            lock.notifyAll();
        }
    }

    private Runnable leaseTimeOutSchedule(String key, Lock lock) {
        return () -> {
            Lock currentLock = locks.get(key);
            if (currentLock == null) {
                return;
            }
            synchronized (currentLock) {
                if (currentLock == lock) {
                    locks.remove(key, currentLock);
                    currentLock.notifyAll();
                }
            }
        };
    }

    private void validateEmptyKey(String key) {
        if (!StringUtils.hasText(key)) {
            throw new BusinessException("락 키는 비어있을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateLockOwner(Lock lock, long currentThreadId) {
        if (!lock.isOwner(currentThreadId)) {
            throw new BusinessException("해당 스레드의 락이 아닙니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateNotExistsLock(Lock lock) {
        if (lock == null) {
            throw new BusinessException("존재하지 않는 락입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateLockTimeOut(long deadline) {
        if (deadline - System.nanoTime() <= 0) {
            throw new BusinessException("락 획득 시간 초과", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private long calculateMillisPart(long nanosLeft) {
        return TimeUnit.NANOSECONDS.toMillis(nanosLeft);
    }

    private int calculateNanosPart(long nanosLeft, long millis) {
        return (int) (nanosLeft - TimeUnit.MILLISECONDS.toNanos(millis));
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
