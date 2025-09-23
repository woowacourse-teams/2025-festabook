package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.timetag.domain.TimeTag;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import java.util.Map;

public record PlaceGeographyResponses(
        @JsonValue List<PlaceGeographyResponse> responses
) {

    public static PlaceGeographyResponses from(List<Place> places, Map<Long, List<TimeTag>> timeTagsMap) {
        return new PlaceGeographyResponses(
                places.stream()
                        .filter(place -> place.getCoordinate() != null)
                        .map(place ->
                                PlaceGeographyResponse.from(
                                        place,
                                        timeTagsMap.getOrDefault(place.getId(), List.of())
                                )
                        )
                        .toList()
        );
    }
}
