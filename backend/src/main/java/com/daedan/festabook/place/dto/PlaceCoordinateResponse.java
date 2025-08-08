package com.daedan.festabook.place.dto;

import com.daedan.festabook.festival.dto.FestivalCoordinateResponse;
import com.daedan.festabook.place.domain.Place;

public record PlaceCoordinateResponse(
        Long id,
        FestivalCoordinateResponse coordinate
) {

    public static PlaceCoordinateResponse from(Place place) {
        return new PlaceCoordinateResponse(
                place.getId(),
                FestivalCoordinateResponse.from(place.getCoordinate())
        );
    }
}
