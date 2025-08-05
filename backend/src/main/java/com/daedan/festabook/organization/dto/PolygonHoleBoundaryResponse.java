package com.daedan.festabook.organization.dto;

import com.daedan.festabook.organization.domain.Coordinate;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record PolygonHoleBoundaryResponse(
        @JsonValue List<CoordinateResponse> responses
) {

    public static PolygonHoleBoundaryResponse from(List<Coordinate> coordinates) {
        return new PolygonHoleBoundaryResponse(
                coordinates.stream()
                        .map(CoordinateResponse::from)
                        .toList()
        );
    }
}
