package com.daedan.festabook.organization.dto;

import com.daedan.festabook.organization.domain.FestivalImage;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record FestivalImageResponses(
        @JsonValue List<FestivalImageResponse> responses
) {

    public static FestivalImageResponses from(List<FestivalImage> festivalImages) {
        return new FestivalImageResponses(
                festivalImages.stream()
                        .map(FestivalImageResponse::from)
                        .toList()
        );
    }
}
