package com.daedan.festabook.orgnaization.domain;

import com.daedan.festabook.organization.domain.Organization;
import java.util.List;
import java.util.stream.IntStream;

public class OrganizationFixture {

    private static final String DEFAULT_NAME = "페스타북";

    public static Organization create() {
        return new Organization(
                DEFAULT_NAME
        );
    }

    public static List<Organization> createList(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> create())
                .toList();
    }
}
