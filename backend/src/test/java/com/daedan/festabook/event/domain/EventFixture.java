package com.daedan.festabook.event.domain;

import com.daedan.festabook.global.fixture.BaseEntityTestHelper;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EventFixture {

    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(12, 0, 0);
    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(13, 0, 0);
    private static final String DEFAULT_TITLE = "title";
    private static final String DEFAULT_LOCATION = "location";

    public static Event create(
            EventDate eventDate
    ) {
        return new Event(
                eventDate,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME,
                DEFAULT_TITLE,
                DEFAULT_LOCATION
        );
    }

    public static Event create(
            EventDate eventDate,
            Long eventId
    ) {
        Event event = new Event(
                eventDate,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME,
                DEFAULT_TITLE,
                DEFAULT_LOCATION
        );
        BaseEntityTestHelper.setId(event, eventId);
        return event;
    }

    public static Event create(
            LocalTime startTime,
            LocalTime endTime,
            EventDate eventDate
    ) {
        return new Event(
                eventDate,
                startTime,
                endTime,
                DEFAULT_TITLE,
                DEFAULT_LOCATION
        );
    }

    public static Event create(
            EventDate eventDate,
            LocalTime startTime,
            LocalTime endTime,
            String title,
            String location
    ) {
        return new Event(
                eventDate,
                startTime,
                endTime,
                title,
                location
        );
    }

    public static Event create(
            EventDate eventDate,
            LocalTime startTime,
            LocalTime endTime,
            String title,
            String location,
            Long eventId
    ) {
        Event event = new Event(
                eventDate,
                startTime,
                endTime,
                title,
                location
        );
        BaseEntityTestHelper.setId(event, eventId);
        return event;
    }

    public static void createWithTitle(String title) {
        new Event(
                EventDateFixture.create(),
                DEFAULT_START_TIME,
                DEFAULT_END_TIME,
                title,
                DEFAULT_LOCATION
        );
    }

    public static void createWithLocation(String location) {
        new Event(
                EventDateFixture.create(),
                DEFAULT_START_TIME,
                DEFAULT_END_TIME,
                DEFAULT_TITLE,
                location
        );
    }

    public static List<Event> createList(int size, EventDate eventDate) {
        return IntStream.range(0, size)
                .mapToObj(i -> create(eventDate))
                .collect(Collectors.toList());
    }
}
