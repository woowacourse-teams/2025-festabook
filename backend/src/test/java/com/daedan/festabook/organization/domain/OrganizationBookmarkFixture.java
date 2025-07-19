package com.daedan.festabook.organization.domain;

import com.daedan.festabook.device.domain.Device;

public class OrganizationBookmarkFixture {

    public static OrganizationBookmark create(
            Long id,
            Organization organization,
            Device device
    ) {
        return new OrganizationBookmark(
                id,
                organization,
                device
        );
    }

    public static OrganizationBookmark create(
            Organization organization,
            Device device
    ) {
        return new OrganizationBookmark(
                organization,
                device
        );
    }
}
