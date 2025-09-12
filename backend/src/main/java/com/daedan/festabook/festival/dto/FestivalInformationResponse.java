package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.Festival;
import java.time.LocalDate;

public record FestivalInformationResponse(
        Long festivalId,
        String festivalName,
        LocalDate startDate,
        LocalDate endDate,
        boolean userVisible
) {

    public static FestivalInformationResponse from(Festival festival) {
        return new FestivalInformationResponse(
                festival.getId(),
                festival.getFestivalName(),
                festival.getStartDate(),
                festival.getEndDate(),
                festival.isUserVisible()
        );
    }
}
