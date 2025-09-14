package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceCategory;
import java.time.LocalTime;

public class MainPlaceUpdateRequestFixture {

    private static final PlaceCategory DEFAULT_CATEGORY = PlaceCategory.FOOD_TRUCK;
    private static final String DEFAULT_TITLE = "수정된 이름";
    private static final String DEFAULT_DESCRIPTION = "수정된 설명";
    private static final String DEFAULT_LOCATION = "수정된 위치";
    private static final String DEFAULT_HOST = "수정된 호스트";
    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(14, 12);
    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(14, 39);

    public static MainPlaceUpdateRequest create() {
        return new MainPlaceUpdateRequest(
                DEFAULT_CATEGORY,
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
    }

    public static MainPlaceUpdateRequest create(
            PlaceCategory placeCategory,
            String title,
            String description,
            String location,
            String host,
            LocalTime startTime,
            LocalTime endTime
    ) {
        return new MainPlaceUpdateRequest(
                placeCategory,
                title,
                description,
                location,
                host,
                startTime,
                endTime
        );
    }
}
