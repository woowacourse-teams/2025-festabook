package com.daedan.festabook.schedule.domain;

import java.time.LocalDate;

public class EventDayFixture {

    private static final LocalDate DEFAULT_DATE = LocalDate.of(2025, 5, 20);

    public static EventDay create() {
        return new EventDay(
                DEFAULT_DATE
        );
    }

    public static EventDay create(LocalDate date) {
        return new EventDay(
                date
        );
    }
}
