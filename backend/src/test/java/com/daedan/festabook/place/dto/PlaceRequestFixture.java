package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceCategory;

public class PlaceRequestFixture {

    private static final PlaceCategory DEFAULT_PLACE_CATEGORY = PlaceCategory.BAR;

    public static PlaceRequest create() {
        return new PlaceRequest(DEFAULT_PLACE_CATEGORY);
    }

    public static PlaceRequest create(
            PlaceCategory placeCategory
    ) {
        return new PlaceRequest(placeCategory);
    }
}
