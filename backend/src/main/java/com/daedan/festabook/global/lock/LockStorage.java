package com.daedan.festabook.global.lock;

import java.util.concurrent.TimeUnit;

public interface LockStorage {

    void tryLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit);

    void unlock(String key);
}
