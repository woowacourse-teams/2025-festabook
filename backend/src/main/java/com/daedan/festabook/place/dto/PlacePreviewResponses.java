package com.daedan.festabook.place.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record PlacePreviewResponses(
        @JsonValue List<PlacePreviewResponse> responses
) {

    public static PlacePreviewResponses from(List<PlacePreviewResponse> responses) {
        return new PlacePreviewResponses(responses);
    }
}
