package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record PlacesCloneResponse(
        @JsonValue List<Long> clonedPlaceIds
) {

    public static PlacesCloneResponse from(List<Place> clonedPlaces) {
        return new PlacesCloneResponse(
                clonedPlaces.stream()
                        .map(Place::getId)
                        .toList()
        );
    }
}
