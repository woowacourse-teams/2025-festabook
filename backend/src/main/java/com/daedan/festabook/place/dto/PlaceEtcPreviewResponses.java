package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record PlaceEtcPreviewResponses(
        @JsonValue List<PlaceEtcPreviewResponse> responses
) {

    public static PlaceEtcPreviewResponses from(List<Place> places) {
        return new PlaceEtcPreviewResponses(
                places.stream()
                        .map(PlaceEtcPreviewResponse::from)
                        .toList()
        );
    }
}
