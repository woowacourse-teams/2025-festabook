package com.daedan.festabook.festival.domain;

public class CoordinateFixture {

    private static final Double DEFAULT_LATITUDE = 37.0;
    private static final Double DEFAULT_LONGITUDE = 127.0;

    public static Coordinate create() {
        return new Coordinate(
                DEFAULT_LATITUDE,
                DEFAULT_LONGITUDE
        );
    }

    public static Coordinate create(
            Double latitude,
            Double longitude
    ) {
        return new Coordinate(
                latitude,
                longitude
        );
    }

    public static Coordinate createWithLatitude(
            Double latitude
    ) {
        return new Coordinate(
                latitude,
                DEFAULT_LONGITUDE
        );
    }

    public static Coordinate createWithLongitude(
            Double longitude
    ) {
        return new Coordinate(
                DEFAULT_LATITUDE,
                longitude
        );
    }
}
