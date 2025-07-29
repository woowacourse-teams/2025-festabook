package com.daedan.festabook.organization.domain;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FestivalImageFixture {

    private static final Organization DEFAULT_ORGANIZATION = OrganizationFixture.create();
    private static final String DEFAULT_IMAGE_URL = "https://example.com/image.jpg";
    private static final Integer DEFAULT_SEQUENCE = 3;

    public static FestivalImage create(
            Organization organization
    ) {
        return new FestivalImage(
                organization,
                DEFAULT_IMAGE_URL,
                DEFAULT_SEQUENCE
        );
    }

    public static List<FestivalImage> createList(int size, Organization organization) {
        return IntStream.range(0, size)
                .mapToObj(i -> create(organization))
                .collect(Collectors.toList());
    }
}
