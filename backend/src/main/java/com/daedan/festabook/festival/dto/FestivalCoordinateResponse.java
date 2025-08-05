package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.Coordinate;

public record FestivalCoordinateResponse(
        Double latitude,
        Double longitude
) {

    public static FestivalCoordinateResponse from(Coordinate coordinate) {
        if (coordinate == null) {
            return new FestivalCoordinateResponse(null, null);
        }
        return new FestivalCoordinateResponse(
                coordinate.getLatitude(),
                coordinate.getLongitude()
        );
    }
}
