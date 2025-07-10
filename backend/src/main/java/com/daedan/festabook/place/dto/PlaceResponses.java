package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record PlaceResponses(
        @JsonValue List<PlaceResponse> responses
) {

    public static PlaceResponses from(List<Place> places) {
        return new PlaceResponses(
                places.stream()
                        .map(PlaceResponse::from)
                        .toList()
        );
    }
}
