package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceCategory;

public class PlaceRequestFixture {

    private static final PlaceCategory DEFAULT_PLACE_CATEGORY = PlaceCategory.BAR;
    private static final String DEFAULT_TITLE = "후문 주차장";

    public static PlaceRequest create() {
        return new PlaceRequest(
                DEFAULT_PLACE_CATEGORY,
                DEFAULT_TITLE
        );
    }

    public static PlaceRequest create(
            PlaceCategory placeCategory,
            String title
    ) {
        return new PlaceRequest(
                placeCategory,
                title
        );
    }
}
