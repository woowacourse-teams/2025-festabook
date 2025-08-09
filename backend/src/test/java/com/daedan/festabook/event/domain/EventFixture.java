package com.daedan.festabook.event.domain;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EventFixture {

    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(12, 0, 0);
    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(13, 0, 0);
    private static final String DEFAULT_TITLE = "title";
    private static final String DEFAULT_LOCATION = "location";
    private static final EventDate DEFAULT_EVENT_DATE = EventDateFixture.create();

    public static Event create(
            Long eventId,
            LocalTime startTime,
            LocalTime endTime,
            String title,
            String location,
            EventDate eventDate
    ) {
        return new Event(
                eventId,
                startTime,
                endTime,
                title,
                location,
                eventDate
        );
    }

    public static Event create(
            String title
    ) {
        return new Event(
                DEFAULT_START_TIME,
                DEFAULT_END_TIME,
                title,
                DEFAULT_LOCATION,
                DEFAULT_EVENT_DATE
        );
    }

    public static Event create() {
        return new Event(
                DEFAULT_START_TIME,
                DEFAULT_END_TIME,
                DEFAULT_TITLE,
                DEFAULT_LOCATION,
                DEFAULT_EVENT_DATE
        );
    }

    public static Event create(
            EventDate eventDate
    ) {
        return new Event(
                DEFAULT_START_TIME,
                DEFAULT_END_TIME,
                DEFAULT_TITLE,
                DEFAULT_LOCATION,
                eventDate
        );
    }

    public static Event create(
            LocalTime startTime,
            LocalTime endTime,
            EventDate eventDate
    ) {
        return new Event(
                startTime,
                endTime,
                DEFAULT_TITLE,
                DEFAULT_LOCATION,
                eventDate
        );
    }

    public static Event create(
            LocalTime startTime,
            LocalTime endTime,
            String title,
            String location,
            EventDate eventDate
    ) {
        return new Event(
                startTime,
                endTime,
                title,
                location,
                eventDate
        );
    }

    public static List<Event> createList(int size, EventDate eventDate) {
        return IntStream.range(0, size)
                .mapToObj(i -> create(eventDate))
                .collect(Collectors.toList());
    }
}
