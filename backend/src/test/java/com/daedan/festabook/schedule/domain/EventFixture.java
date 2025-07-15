package com.daedan.festabook.schedule.domain;

import static com.daedan.festabook.schedule.domain.EventStatus.ONGOING;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EventFixture {

    private static final EventStatus DEFAULT_STATUS = ONGOING;
    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(12, 0, 0);
    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(13, 0, 0);
    private static final String DEFAULT_TITLE = "title";
    private static final String DEFAULT_LOCATION = "location";

    public static Event create(
            EventDate eventDate
    ) {
        return new Event(
                DEFAULT_STATUS,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME,
                DEFAULT_TITLE,
                DEFAULT_LOCATION,
                eventDate
        );
    }

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

    public static Event create(
            LocalTime startTime,
            LocalTime endTime,
            EventDate eventDate
    ) {
        return new Event(
                DEFAULT_STATUS,
                startTime,
                endTime,
                DEFAULT_TITLE,
                DEFAULT_LOCATION,
                eventDate
        );
    }

    public static List<Event> createList(int size, EventDate eventDate) {
        return IntStream.range(0, size)
                .mapToObj(i -> create(eventDate))
                .collect(Collectors.toList());
    }
}
