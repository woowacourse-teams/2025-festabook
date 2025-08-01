package com.daedan.festabook.organization.dto;

import java.time.LocalDate;

public record OrganizationResponse(
        Long id,
        String universityName,
        FestivalImageResponses festivalImages,
        String festivalName,
        LocalDate startDate,
        LocalDate endDate
) {
}
