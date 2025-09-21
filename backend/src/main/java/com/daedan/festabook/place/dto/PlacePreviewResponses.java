package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.timetag.domain.TimeTag;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import java.util.Map;

public record PlacePreviewResponses(
        @JsonValue List<PlacePreviewResponse> responses
) {

    public static PlacePreviewResponses from(
            List<PlacePreviewResponse> responses
    ) {
        return new PlacePreviewResponses(responses);
    }

    public static PlacePreviewResponses from(
            List<Place> places,
            Map<Long, PlaceImage> images,
            Map<Long, List<TimeTag>> timeTagsMap
    ) {
        return new PlacePreviewResponses(
                places.stream()
                        .map(place -> PlacePreviewResponse.from(
                                place,
                                images.getOrDefault(place.getId(), null),
                                timeTagsMap.getOrDefault(place.getId(), List.of())
                        ))
                        .toList()
        );
    }
}
