package com.daedan.festabook.organization.dto;

import com.daedan.festabook.organization.domain.FestivalImage;
import com.daedan.festabook.organization.domain.Organization;
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

    public static FestivalResponse from(Organization organization, List<FestivalImage> festivalImages) {
        return new FestivalResponse(
                organization.getId(),
                organization.getUniversityName(),
                FestivalImageResponses.from(festivalImages),
                organization.getFestivalName(),
                organization.getStartDate(),
                organization.getEndDate()
        );
    }
}
