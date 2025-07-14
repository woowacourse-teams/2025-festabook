package com.daedan.festabook.schedule.domain;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import java.time.LocalDate;

public class EventDateFixture {

    private static final Organization DEFAULT_ORGANIZATION = OrganizationFixture.create();
    private static final LocalDate DEFAULT_DATE = LocalDate.of(2025, 5, 20);

    public static EventDate create() {
        return new EventDate(
                DEFAULT_ORGANIZATION,
                DEFAULT_DATE
        );
    }

    public static EventDate create(
            LocalDate date
    ) {
        return new EventDate(
                DEFAULT_ORGANIZATION,
                date
        );
    }
}
