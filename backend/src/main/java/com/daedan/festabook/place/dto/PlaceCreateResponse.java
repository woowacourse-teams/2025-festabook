package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;

public record PlaceCreateResponse(
        Long placeId,
        PlaceCategory category,
        String title
) {

    public static PlaceCreateResponse from(Place place) {
        return new PlaceCreateResponse(
                place.getId(),
                place.getCategory(),
                place.getTitle()
        );
    }
}
