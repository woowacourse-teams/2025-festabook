package com.daedan.festabook.organization.dto;

import com.daedan.festabook.organization.domain.Coordinate;

public record OrganizationCoordinateResponse(
        Double latitude,
        Double longitude
) {

    public static OrganizationCoordinateResponse from(Coordinate coordinate) {
        return new OrganizationCoordinateResponse(
                coordinate.getLatitude(),
                coordinate.getLongitude()
        );
    }
}
