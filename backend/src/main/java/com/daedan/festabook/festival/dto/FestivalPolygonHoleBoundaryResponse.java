package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.Coordinate;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record FestivalPolygonHoleBoundaryResponse(
        @JsonValue List<FestivalCoordinateResponse> responses
) {

    public static FestivalPolygonHoleBoundaryResponse from(List<Coordinate> coordinates) {
        return new FestivalPolygonHoleBoundaryResponse(
                coordinates.stream()
                        .map(FestivalCoordinateResponse::from)
                        .toList()
        );
    }
}
