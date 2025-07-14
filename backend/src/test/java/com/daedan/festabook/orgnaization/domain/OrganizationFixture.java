package com.daedan.festabook.orgnaization.domain;

import com.daedan.festabook.organization.domain.Organization;

public class OrganizationFixture {

    private static final String DEFAULT_NAME = "페스타북";

    public static Organization create() {
        return new Organization(
                DEFAULT_NAME
        );
    }
}
