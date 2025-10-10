package com.daedan.festabook.global.lock.infrastructure;

import java.util.concurrent.ScheduledFuture;

public class Lock {
    private volatile long ownerThreadId;
    private volatile ScheduledFuture<?> leaseTimeOutSchedule;

    public synchronized void registerOwnerThreadId(long ownerThreadId) {
        this.ownerThreadId = ownerThreadId;
    }

    public synchronized void registerLeaseTimeOutSchedule(ScheduledFuture<?> leaseTimeOutSchedule) {
        cancelIfExistsLeaseTimeOutSchedule();
        this.leaseTimeOutSchedule = leaseTimeOutSchedule;
    }

    public synchronized void cancelIfExistsLeaseTimeOutSchedule() {
        if (leaseTimeOutSchedule != null) {
            leaseTimeOutSchedule.cancel(false);
        }
    }

    public synchronized boolean isOwner(long threadId) {
        return ownerThreadId == threadId;
    }
}
