package com.daedan.festabook.event.dto;

import java.time.LocalTime;

public class EventUpdateRequestFixture {

    private final static Long DEFAULT_EVENT_DATE_ID = 1L;
    private final static LocalTime DEFAULT_START_TIME = LocalTime.of(1, 0);
    private final static LocalTime DEFAULT_END_TIME = LocalTime.of(2, 0);
    private final static String DEFAULT_TITLE = "title";
    private final static String DEFAULT_LOCATION = "location";

    public static EventUpdateRequest create() {
        return new EventUpdateRequest(
                DEFAULT_EVENT_DATE_ID,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME,
                DEFAULT_TITLE,
                DEFAULT_LOCATION
        );
    }

    public static EventUpdateRequest create(
            Long eventDateId
    ) {
        return new EventUpdateRequest(
                eventDateId,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME,
                DEFAULT_TITLE,
                DEFAULT_LOCATION
        );
    }

    public static EventUpdateRequest create(
            Long eventDateId,
            LocalTime startTime,
            LocalTime endTime,
            String title,
            String location
    ) {
        return new EventUpdateRequest(
                eventDateId,
                startTime,
                endTime,
                title,
                location
        );
    }
}
