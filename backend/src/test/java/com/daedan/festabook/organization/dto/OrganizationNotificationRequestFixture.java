package com.daedan.festabook.organization.dto;

public class OrganizationNotificationRequestFixture {

    public static OrganizationNotificationRequest create(
            Long deviceId
    ) {
        return new OrganizationNotificationRequest(
                deviceId
        );
    }
}
