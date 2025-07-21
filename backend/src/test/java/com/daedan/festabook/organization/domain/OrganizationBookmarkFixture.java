package com.daedan.festabook.organization.domain;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;

public class OrganizationBookmarkFixture {

    private static final Organization DEFAULT_ORGANIZATION = OrganizationFixture.create();
    private static final Device DEFAULT_DEVICE = DeviceFixture.create();

    public static OrganizationBookmark create(
            Long id
    ) {
        return new OrganizationBookmark(
                id,
                DEFAULT_ORGANIZATION,
                DEFAULT_DEVICE
        );
    }

    public static OrganizationBookmark create(
            Long id,
            Organization organization
    ) {
        return new OrganizationBookmark(
                id,
                organization,
                DEFAULT_DEVICE
        );
    }

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
