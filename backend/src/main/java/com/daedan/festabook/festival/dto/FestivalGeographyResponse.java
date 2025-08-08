package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.Festival;

public record FestivalGeographyResponse(
        Integer zoom,
        FestivalCoordinateResponse centerCoordinate,
        FestivalPolygonHoleBoundaryResponse polygonHoleBoundary
) {

    public static FestivalGeographyResponse from(Festival festival) {
        return new FestivalGeographyResponse(
                festival.getZoom(),
                FestivalCoordinateResponse.from(festival.getCenterCoordinate()),
                FestivalPolygonHoleBoundaryResponse.from(festival.getPolygonHoleBoundary())
        );
    }
}
