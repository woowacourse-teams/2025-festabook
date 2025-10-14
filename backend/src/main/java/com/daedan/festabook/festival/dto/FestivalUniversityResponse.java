package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.Festival;
import java.time.LocalDate;

public record FestivalUniversityResponse(
        Long festivalId,
        String universityName,
        String festivalName,
        LocalDate startDate,
        LocalDate endDate
) {

    public static FestivalUniversityResponse from(Festival festival) {
        return new FestivalUniversityResponse(
                festival.getId(),
                festival.getUniversityName(),
                festival.getFestivalName(),
                festival.getStartDate(),
                festival.getEndDate()
        );
    }
}
