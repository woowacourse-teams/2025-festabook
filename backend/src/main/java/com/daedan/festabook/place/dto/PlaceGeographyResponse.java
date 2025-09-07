package com.daedan.festabook.place.dto;

import com.daedan.festabook.festival.dto.FestivalCoordinateResponse;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;

public record PlaceGeographyResponse(
        Long placeId,
        PlaceCategory category,
        FestivalCoordinateResponse markerCoordinate,
        String title
) {

    public static PlaceGeographyResponse from(Place place) {
        return new PlaceGeographyResponse(
                place.getId(),
                place.getCategory(),
                FestivalCoordinateResponse.from(place.getCoordinate()),
                place.getTitle()
        );
    }
}
