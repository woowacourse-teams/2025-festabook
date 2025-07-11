package com.daedan.festabook.schedule.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventDayFixture {

    private static final List<Event> EMPTY_EVENT_LIST = new ArrayList<>();

    public static EventDay create(LocalDate date) {
        return new EventDay(
                date,
                EMPTY_EVENT_LIST
        );
    }
}
