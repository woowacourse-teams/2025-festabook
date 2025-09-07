package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.Festival;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record FestivalUniversityResponses(
        @JsonValue List<FestivalUniversityResponse> responses
) {

    public static FestivalUniversityResponses from(List<Festival> festivals) {
        return new FestivalUniversityResponses(
                festivals.stream()
                        .map(FestivalUniversityResponse::from)
                        .toList()
        );
    }
}
