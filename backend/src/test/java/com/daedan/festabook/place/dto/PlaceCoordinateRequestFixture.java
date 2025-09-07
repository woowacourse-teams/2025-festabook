package com.daedan.festabook.place.dto;

public class PlaceCoordinateRequestFixture {

    private static final Double DEFAULT_LATITUDE = 37.1;
    private static final Double DEFAULT_LONGITUDE = 127.1;

    public static PlaceCoordinateRequest create() {
        return new PlaceCoordinateRequest(
                DEFAULT_LATITUDE,
                DEFAULT_LONGITUDE
        );
    }
}
