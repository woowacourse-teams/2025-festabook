package com.daedan.festabook.schedule.domain;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

public class EventDateFixture {

    private static final Organization DEFAULT_ORGANIZATION = OrganizationFixture.create();
    private static final LocalDate DEFAULT_DATE = LocalDate.of(2025, 5, 20);

    public static EventDate create(
            Long eventDateId,
            LocalDate date
    ) {
        return new EventDate(
                eventDateId,
                DEFAULT_ORGANIZATION,
                date
        );
    }

    public static EventDate create() {
        return new EventDate(
                DEFAULT_ORGANIZATION,
                DEFAULT_DATE
        );
    }

    public static EventDate create(
            Organization organization
    ) {
        return new EventDate(
                organization,
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

    public static EventDate create(
            Organization organization,
            LocalDate date
    ) {
        return new EventDate(
                organization,
                date
        );
    }

    public static List<EventDate> createList(int size, Organization organization) {
        return IntStream.range(0, size)
                .mapToObj(i -> create(organization))
                .toList();
    }

    public static List<EventDate> createList(List<LocalDate> dates, Organization organization) {
        return dates.stream()
                .map(date -> create(organization, date))
                .toList();
    }
}
