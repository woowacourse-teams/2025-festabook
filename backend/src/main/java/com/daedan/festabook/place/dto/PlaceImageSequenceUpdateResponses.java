package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceImage;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record PlaceImageSequenceUpdateResponses(
        @JsonValue List<PlaceImageSequenceUpdateResponse> responses
) {

    public static PlaceImageSequenceUpdateResponses from(List<PlaceImage> placeImages) {
        return new PlaceImageSequenceUpdateResponses(
                placeImages.stream()
                        .map(PlaceImageSequenceUpdateResponse::from)
                        .toList()
        );
    }
}
