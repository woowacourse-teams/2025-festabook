package com.daedan.festabook.schedule.domain;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;

public enum EventStatus {
    COMPLETED,
    ONGOING,
    UPCOMING;

    public static EventStatus determine(Clock clock, LocalDate date, LocalTime startTime, LocalTime endTime) {
        LocalDate today = LocalDate.now(clock);

        if (date.isBefore(today)) {
            return COMPLETED;
        }

        if (date.isAfter(today)) {
            return UPCOMING;
        }

        return determineToday(clock, startTime, endTime);
    }

    private static EventStatus determineToday(Clock clock, LocalTime startTime, LocalTime endTime) {
        LocalTime now = LocalTime.now(clock);

        if (now.isBefore(startTime)) {
            return UPCOMING;
        }

        if (now.isAfter(endTime)) {
            return COMPLETED;
        }

        return ONGOING;
    }
}
