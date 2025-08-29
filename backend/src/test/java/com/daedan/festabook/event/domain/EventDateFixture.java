package com.daedan.festabook.event.domain;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.fixture.BaseEntityTestHelper;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

public class EventDateFixture {

    private static final Festival DEFAULT_FESTIVAL = FestivalFixture.create();
    private static final LocalDate DEFAULT_DATE = LocalDate.of(2025, 5, 20);

    public static EventDate create(
            Long eventDateId,
            LocalDate date
    ) {
        EventDate eventDate = new EventDate(
                DEFAULT_FESTIVAL,
                date
        );
        BaseEntityTestHelper.setId(eventDate, eventDateId);
        return eventDate;
    }

    public static EventDate create(
            Long eventDateId,
            Festival festival,
            LocalDate date
    ) {
        EventDate eventDate = new EventDate(
                festival,
                date
        );
        BaseEntityTestHelper.setId(eventDate, eventDateId);
        return eventDate;
    }

    public static EventDate create(
            Long eventDateId,
            Festival festival
    ) {
        EventDate eventDate = new EventDate(
                festival,
                DEFAULT_DATE
        );
        BaseEntityTestHelper.setId(eventDate, eventDateId);
        return eventDate;
    }

    public static EventDate create() {
        return new EventDate(
                DEFAULT_FESTIVAL,
                DEFAULT_DATE
        );
    }

    public static EventDate create(
            Festival festival
    ) {
        return new EventDate(
                festival,
                DEFAULT_DATE
        );
    }

    public static EventDate create(
            LocalDate date
    ) {
        return new EventDate(
                DEFAULT_FESTIVAL,
                date
        );
    }

    public static EventDate create(
            Festival festival,
            LocalDate date
    ) {
        return new EventDate(
                festival,
                date
        );
    }

    public static List<EventDate> createList(int size, Festival festival) {
        return IntStream.range(0, size)
                .mapToObj(i -> create(festival))
                .toList();
    }

    public static List<EventDate> createList(List<LocalDate> dates, Festival festival) {
        return dates.stream()
                .map(date -> create(festival, date))
                .toList();
    }
}
