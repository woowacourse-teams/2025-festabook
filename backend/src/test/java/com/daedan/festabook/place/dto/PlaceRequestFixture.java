package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceCategory;

public class PlaceRequestFixture {

    public static PlaceRequest create(
            PlaceCategory placeCategory
    ) {
        return new PlaceRequest(placeCategory);
    }
}
