package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;

public record PlaceExtraUpdateResponse(
        String title
) {

    public static PlaceExtraUpdateResponse from(Place place) {
        return new PlaceExtraUpdateResponse(
                place.getTitle()
        );
    }
}
