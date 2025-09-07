package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.Festival;

public record FestivalUniversityResponse(
        Long festivalId,
        String universityName
) {

    public static FestivalUniversityResponse from(Festival festival) {
        return new FestivalUniversityResponse(
                festival.getId(),
                festival.getUniversityName()
        );
    }
}
