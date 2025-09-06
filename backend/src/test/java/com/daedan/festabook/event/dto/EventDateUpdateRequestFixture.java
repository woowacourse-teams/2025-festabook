package com.daedan.festabook.event.dto;

import java.time.LocalDate;

public class EventDateUpdateRequestFixture {

    private final static LocalDate DEFAULT_DATE = LocalDate.of(2025, 7, 18);

    public static EventDateUpdateRequest create() {
        return new EventDateUpdateRequest(
                DEFAULT_DATE
        );
    }

    public static EventDateUpdateRequest create(
            LocalDate date
    ) {
        return new EventDateUpdateRequest(
                date
        );
    }
}
