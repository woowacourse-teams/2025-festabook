package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.FestivalImage;
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
