package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record PlaceGeographyResponses(
        @JsonValue List<PlaceGeographyResponse> responses
) {

    public static PlaceGeographyResponses from(List<Place> places) {
        return new PlaceGeographyResponses(
                places.stream()
                        .map(PlaceGeographyResponse::from)
                        .toList()
        );
    }
}
