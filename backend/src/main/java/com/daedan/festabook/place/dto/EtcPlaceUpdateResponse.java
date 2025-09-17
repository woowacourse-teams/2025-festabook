package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;

public record EtcPlaceUpdateResponse(
        String title
) {

    public static EtcPlaceUpdateResponse from(Place place) {
        return new EtcPlaceUpdateResponse(
                place.getTitle()
        );
    }
}
