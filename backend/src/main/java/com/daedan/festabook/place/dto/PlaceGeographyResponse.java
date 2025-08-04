package com.daedan.festabook.place.dto;

import com.daedan.festabook.organization.dto.CoordinateResponse;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;

public record PlaceGeographyResponse(
        Long id,
        PlaceCategory category,
        CoordinateResponse markerCoordinate
) {

    public static PlaceGeographyResponse from(Place place) {
        return new PlaceGeographyResponse(
                place.getId(),
                place.getCategory(),
                CoordinateResponse.from(place.getCoordinate())
        );
    }
}
