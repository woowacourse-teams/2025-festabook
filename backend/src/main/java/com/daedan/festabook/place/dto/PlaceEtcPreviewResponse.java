package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;

public record PlaceEtcPreviewResponse(
        Long placeId,
        String title
) {

    public static PlaceEtcPreviewResponse from(Place place) {
        return new PlaceEtcPreviewResponse(
                place.getId(),
                place.getTitle()
        );
    }
}
