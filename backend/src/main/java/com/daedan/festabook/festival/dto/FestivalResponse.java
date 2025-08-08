package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalImage;
import java.time.LocalDate;
import java.util.List;

public record FestivalResponse(
        Long id,
        String universityName,
        FestivalImageResponses festivalImages,
        String festivalName,
        LocalDate startDate,
        LocalDate endDate
) {

    public static FestivalResponse from(Festival festival, List<FestivalImage> festivalImages) {
        return new FestivalResponse(
                festival.getId(),
                festival.getUniversityName(),
                FestivalImageResponses.from(festivalImages),
                festival.getFestivalName(),
                festival.getStartDate(),
                festival.getEndDate()
        );
    }
}
