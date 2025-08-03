package com.daedan.festabook.organization.dto;

import java.time.LocalDate;

public class OrganizationInformationUpdateRequestFixture {

    private static final String DEFAULT_FESTIVAL_NAME = "축제 이름";
    private static final LocalDate DEFAULT_START_DATE = LocalDate.of(2025, 10, 1);
    private static final LocalDate DEFAULT_END_DATE = LocalDate.of(2025, 10, 2);

    public static OrganizationInformationUpdateRequest create() {
        return new OrganizationInformationUpdateRequest(
                DEFAULT_FESTIVAL_NAME,
                DEFAULT_START_DATE,
                DEFAULT_END_DATE
        );
    }

    public static OrganizationInformationUpdateRequest create(
            String festivalName,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return new OrganizationInformationUpdateRequest(
                festivalName,
                startDate,
                endDate
        );
    }
}
