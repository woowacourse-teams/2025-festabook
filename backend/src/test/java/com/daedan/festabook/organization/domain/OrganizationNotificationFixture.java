package com.daedan.festabook.organization.domain;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;

public class OrganizationNotificationFixture {

    private static final Organization DEFAULT_ORGANIZATION = OrganizationFixture.create();
    private static final Device DEFAULT_DEVICE = DeviceFixture.create();

    public static OrganizationNotification create(
            Long id
    ) {
        return new OrganizationNotification(
                id,
                DEFAULT_ORGANIZATION,
                DEFAULT_DEVICE
        );
    }

    public static OrganizationNotification create(
            Long id,
            Organization organization
    ) {
        return new OrganizationNotification(
                id,
                organization,
                DEFAULT_DEVICE
        );
    }

    public static OrganizationNotification create(
            Long id,
            Organization organization,
            Device device
    ) {
        return new OrganizationNotification(
                id,
                organization,
                device
        );
    }

    public static OrganizationNotification create(
            Organization organization,
            Device device
    ) {
        return new OrganizationNotification(
                organization,
                device
        );
    }
}
