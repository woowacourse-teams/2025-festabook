package com.daedan.festabook.organization.dto;

import com.daedan.festabook.organization.domain.Coordinate;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record OrganizationPolygonHoleBoundaryResponse(
        @JsonValue List<OrganizationCoordinateResponse> response
) {

    public static OrganizationPolygonHoleBoundaryResponse from(List<Coordinate> coordinates) {
        return new OrganizationPolygonHoleBoundaryResponse(
                coordinates.stream()
                        .map(OrganizationCoordinateResponse::from)
                        .toList()
        );
    }
}
