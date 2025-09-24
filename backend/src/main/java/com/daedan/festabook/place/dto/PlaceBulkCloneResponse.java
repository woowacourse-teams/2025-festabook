package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record PlaceBulkCloneResponse(
        @JsonValue List<Long> clonedPlaceIds
) {

    public static PlaceBulkCloneResponse from(List<Place> clonedPlaces) {
        return new PlaceBulkCloneResponse(
                clonedPlaces.stream()
                        .map(Place::getId)
                        .toList()
        );
    }
}
