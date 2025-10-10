package com.daedan.festabook.global.lock.infrastructure;

import java.util.concurrent.ScheduledFuture;

public class Lock {
    private volatile long ownerThreadId;
    private volatile ScheduledFuture<?> leaseTimeOutSchedule;

    public void registerOwnerThreadId(long ownerThreadId) {
        this.ownerThreadId = ownerThreadId;
    }

    public void registerLeaseTimeOutSchedule(ScheduledFuture<?> leaseTimeOutSchedule) {
        cancelIfExistsLeaseTimeOutSchedule();
        this.leaseTimeOutSchedule = leaseTimeOutSchedule;
    }

    public void cancelIfExistsLeaseTimeOutSchedule() {
        if (leaseTimeOutSchedule != null) {
            leaseTimeOutSchedule.cancel(false);
        }
    }

    public boolean isOwner(long threadId) {
        return ownerThreadId == threadId;
    }
}
