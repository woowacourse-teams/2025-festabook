package com.daedan.festabook.organization.dto;

import com.daedan.festabook.organization.domain.Coordinate;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record OrganizationPolygonHoleBoundaryResponse(
        @JsonValue List<CoordinateResponse> responses
) {

    public static OrganizationPolygonHoleBoundaryResponse from(List<Coordinate> coordinates) {
        return new OrganizationPolygonHoleBoundaryResponse(
                coordinates.stream()
                        .map(CoordinateResponse::from)
                        .toList()
        );
    }
}
