package com.daedan.festabook.organization.dto;

import com.daedan.festabook.organization.domain.Organization;
import java.time.LocalDate;

public record OrganizationInformationResponse(
        Long organizationId,
        String festivalName,
        LocalDate startDate,
        LocalDate endDate
) {

    public static OrganizationInformationResponse from(Organization organization) {
        return new OrganizationInformationResponse(
                organization.getId(),
                organization.getFestivalName(),
                organization.getStartDate(),
                organization.getEndDate()
        );
    }
}
