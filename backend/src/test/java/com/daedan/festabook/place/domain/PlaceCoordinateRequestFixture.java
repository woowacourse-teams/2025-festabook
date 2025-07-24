package com.daedan.festabook.place.domain;

import com.daedan.festabook.place.dto.PlaceCoordinateRequest;

public class PlaceCoordinateRequestFixture {

    private static final Double DEFAULT_LATITUDE = 37.1;
    private static final Double DEFAULT_LONGITUDE = 127.1;

    public static PlaceCoordinateRequest create(
            Double latitude,
            Double longitude
    ) {
        return new PlaceCoordinateRequest(
                latitude,
                longitude
        );
    }

    public static PlaceCoordinateRequest create() {
        return new PlaceCoordinateRequest(
                DEFAULT_LATITUDE,
                DEFAULT_LONGITUDE
        );
    }
}
