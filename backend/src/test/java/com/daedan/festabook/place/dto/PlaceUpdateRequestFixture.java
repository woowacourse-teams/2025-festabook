package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceCategory;
import java.time.LocalTime;

public class PlaceUpdateRequestFixture {

    private static final PlaceCategory DEFAULT_CATEGORY = PlaceCategory.BOOTH;
    private static final String DEFAULT_TITLE = "코딩하며 한잔";
    private static final String DEFAULT_DESCRIPTION = "시원한 맥주와 맛있는 치킨!";
    private static final String DEFAULT_LOCATION = "공학관 앞";
    private static final String DEFAULT_HOST = "C블C블";
    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(9, 0);
    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(18, 0);

    public static PlaceUpdateRequest create() {
        return new PlaceUpdateRequest(
                DEFAULT_CATEGORY,
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
    }
}
