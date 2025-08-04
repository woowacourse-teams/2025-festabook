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
                        .filter(place -> place.getCoordinate() != null)
                        .map(PlaceGeographyResponse::from)
                        .toList()
        );
    }
}
