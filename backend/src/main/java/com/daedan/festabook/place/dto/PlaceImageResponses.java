package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceImage;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record PlaceImageResponses(
        @JsonValue List<PlaceImageResponse> responses
) {

    public static PlaceImageResponses from(List<PlaceImage> placeImages) {
        return new PlaceImageResponses(
                placeImages.stream()
                        .map(PlaceImageResponse::from)
                        .toList()
        );
    }
}
