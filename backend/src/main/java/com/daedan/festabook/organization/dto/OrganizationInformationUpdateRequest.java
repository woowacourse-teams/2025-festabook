package com.daedan.festabook.organization.dto;

import java.time.LocalDate;

public record OrganizationInformationUpdateRequest(
        String festivalName,
        LocalDate startDate,
        LocalDate endDate
) {
}
