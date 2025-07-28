package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceCategory;

public class MainPlaceRequestFixture {

    public static MainPlaceRequest create(PlaceCategory placeCategory, String title) {
        return new MainPlaceRequest(
                placeCategory,
                title
        );
    }
}
