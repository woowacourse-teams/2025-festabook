package com.daedan.festabook.place.dto;

import com.daedan.festabook.organization.dto.CoordinateResponse;
import com.daedan.festabook.place.domain.Place;

public record PlaceCoordinateResponse(
        Long id,
        CoordinateResponse coordinate
) {

    public static PlaceCoordinateResponse from(Place place) {
        return new PlaceCoordinateResponse(
                place.getId(),
                CoordinateResponse.from(place.getCoordinate())
        );
    }
}
