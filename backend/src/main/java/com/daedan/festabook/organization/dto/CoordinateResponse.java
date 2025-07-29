package com.daedan.festabook.organization.dto;

import com.daedan.festabook.organization.domain.Coordinate;

public record CoordinateResponse(
        Double latitude,
        Double longitude
) {

    public static CoordinateResponse from(Coordinate coordinate) {
        if (coordinate == null) {
            return null;
        }
        return new CoordinateResponse(
                coordinate.getLatitude(),
                coordinate.getLongitude()
        );
    }
}
