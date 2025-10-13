package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceCategory;

public class PlaceCreateRequestFixture {

    private static final PlaceCategory DEFAULT_PLACE_CATEGORY = PlaceCategory.BAR;
    private static final String DEFAULT_TITLE = "후문 주차장";

    public static PlaceCreateRequest create() {
        return new PlaceCreateRequest(
                DEFAULT_PLACE_CATEGORY,
                DEFAULT_TITLE
        );
    }

    public static PlaceCreateRequest create(
            PlaceCategory placeCategory,
            String title
    ) {
        return new PlaceCreateRequest(
                placeCategory,
                title
        );
    }
}
