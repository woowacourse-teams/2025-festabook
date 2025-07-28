package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceCategory;

public class EtcPlaceRequestFixture {

    public static EtcPlaceRequest create(PlaceCategory placeCategory) {
        return new EtcPlaceRequest(placeCategory);
    }
}
