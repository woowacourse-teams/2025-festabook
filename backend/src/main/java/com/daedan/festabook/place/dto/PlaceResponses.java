package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalTime;
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
