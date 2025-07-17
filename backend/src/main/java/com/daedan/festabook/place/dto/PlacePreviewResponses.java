package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceDetail;
import com.daedan.festabook.place.domain.PlaceImage;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import java.util.Map;

public record PlacePreviewResponses(
        @JsonValue List<PlacePreviewResponse> responses
) {

    public static PlacePreviewResponses from(List<Place> places, Map<Long, PlaceDetail> placeDetails,
                                             Map<Long, PlaceImage> images) {
        return new PlacePreviewResponses(
                places.stream()
                        .map(place -> PlacePreviewResponse.from(
                                place,
                                placeDetails.get(place.getId()),
                                images.get(place.getId())
                        ))
                        .toList()
        );
    }
}
