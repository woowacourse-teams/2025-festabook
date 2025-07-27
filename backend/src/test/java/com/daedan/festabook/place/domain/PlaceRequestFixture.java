package com.daedan.festabook.place.domain;

import com.daedan.festabook.place.dto.PlaceRequest;

public class PlaceRequestFixture {

    public static PlaceRequest create(PlaceCategory placeCategory, String title) {
        return new PlaceRequest(
                placeCategory,
                title
        );
    }

    public static PlaceRequest createEmpty(PlaceCategory placeCategory) {
        return new PlaceRequest(
                placeCategory,
                null
        );
    }
}
