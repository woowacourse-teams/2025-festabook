package com.daedan.festabook.place.dto;

import com.daedan.festabook.festival.domain.Coordinate;

public record PlaceCoordinateRequest(
        Double latitude,
        Double longitude
) {

    public Coordinate toCoordinate() {
        return new Coordinate(
                latitude,
                longitude
        );
    }
}
