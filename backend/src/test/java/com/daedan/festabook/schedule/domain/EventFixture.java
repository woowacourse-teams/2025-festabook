package com.daedan.festabook.schedule.domain;

import java.time.LocalTime;

public class EventFixture {

    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(12, 0, 0);
    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(13, 0, 0);
    private static final String DEFAULT_LOCATION = "location";

    public static Event create(
            EventStatus status,
            String title
    ) {
        return new Event(
                status,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME,
                title,
                DEFAULT_LOCATION,
                EventDateFixture.create()
        );
    }
}
