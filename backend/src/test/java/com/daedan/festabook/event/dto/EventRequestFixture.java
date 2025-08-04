package com.daedan.festabook.event.dto;

import java.time.LocalTime;

public class EventRequestFixture {

    private final static LocalTime DEFAULT_START_TIME = LocalTime.of(1, 0);
    private final static LocalTime DEFAULT_END_TIME = LocalTime.of(2, 0);
    private final static String DEFAULT_TITLE = "title";
    private final static String DEFAULT_LOCATION = "location";
    private final static Long DEFAULT_EVENT_DATE_ID = 1L;

    public static EventRequest create(
            LocalTime startTime,
            LocalTime endTime,
            String title,
            String location,
            Long eventDateId
    ) {
        return new EventRequest(
                startTime,
                endTime,
                title,
                location,
                eventDateId
        );
    }

    public static EventRequest create(
            Long eventDateId
    ) {
        return new EventRequest(
                DEFAULT_START_TIME,
                DEFAULT_END_TIME,
                DEFAULT_TITLE,
                DEFAULT_LOCATION,
                eventDateId
        );
    }

    public static EventRequest create() {
        return new EventRequest(
                DEFAULT_START_TIME,
                DEFAULT_END_TIME,
                DEFAULT_TITLE,
                DEFAULT_LOCATION,
                DEFAULT_EVENT_DATE_ID
        );
    }
}
