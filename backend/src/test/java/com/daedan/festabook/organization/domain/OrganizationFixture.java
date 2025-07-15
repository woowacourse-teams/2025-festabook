package com.daedan.festabook.organization.domain;

public class OrganizationFixture {

    private static final String DEFAULT_NAME = "페스타북";

    public static Organization create(
            String name
    ) {
        return new Organization(name);
    }

    public static Organization create() {
        return new Organization(
                DEFAULT_NAME
        );
    }
}
