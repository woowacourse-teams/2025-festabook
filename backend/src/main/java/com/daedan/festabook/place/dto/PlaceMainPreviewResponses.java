package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceImage;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import java.util.Map;

public record PlaceMainPreviewResponses(
        @JsonValue List<PlaceMainPreviewResponse> responses
) {

    public static PlaceMainPreviewResponses from(
            List<PlaceMainPreviewResponse> responses
    ) {
        return new PlaceMainPreviewResponses(responses);
    }

    public static PlaceMainPreviewResponses from(
            List<Place> places,
            Map<Long, PlaceImage> images
    ) {
        return new PlaceMainPreviewResponses(
                places.stream()
                        .map(place -> PlaceMainPreviewResponse.from(
                                place,
                                images.getOrDefault(place.getId(), null)
                        ))
                        .toList()
        );
    }
}
