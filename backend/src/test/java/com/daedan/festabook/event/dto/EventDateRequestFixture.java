package com.daedan.festabook.event.dto;

import java.time.LocalDate;

public class EventDateRequestFixture {

    private final static LocalDate DEFAULT_DATE = LocalDate.of(2025, 7, 18);

    public static EventDateRequest create() {
        return new EventDateRequest(
                DEFAULT_DATE
        );
    }
}
