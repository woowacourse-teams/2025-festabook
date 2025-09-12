package com.daedan.festabook.festival.dto;

import java.time.LocalDate;

public class FestivalInformationUpdateRequestFixture {

    private static final String DEFAULT_FESTIVAL_NAME = "축제 이름";
    private static final LocalDate DEFAULT_START_DATE = LocalDate.of(2025, 10, 1);
    private static final LocalDate DEFAULT_END_DATE = LocalDate.of(2025, 10, 2);
    private static final boolean DEFAULT_USER_VISIBLE = false;

    public static FestivalInformationUpdateRequest create() {
        return new FestivalInformationUpdateRequest(
                DEFAULT_FESTIVAL_NAME,
                DEFAULT_START_DATE,
                DEFAULT_END_DATE,
                DEFAULT_USER_VISIBLE
        );
    }

    public static FestivalInformationUpdateRequest create(
            String festivalName,
            LocalDate startDate,
            LocalDate endDate,
            boolean DEFAULT_USER_VISIBLE
    ) {
        return new FestivalInformationUpdateRequest(
                festivalName,
                startDate,
                endDate,
                DEFAULT_USER_VISIBLE
        );
    }
}
