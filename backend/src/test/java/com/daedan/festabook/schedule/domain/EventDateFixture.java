package com.daedan.festabook.schedule.domain;

import java.time.LocalDate;

public class EventDateFixture {

    private static final LocalDate DEFAULT_DATE = LocalDate.of(2025, 5, 20);

    public static EventDate create() {
        return new EventDate(
                DEFAULT_DATE
        );
    }

    public static EventDate create(
            LocalDate date
    ) {
        return new EventDate(
                date
        );
    }
}
