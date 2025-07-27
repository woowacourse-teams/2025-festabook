package com.daedan.festabook.organization.domain;

import com.daedan.festabook.organization.dto.OrganizationNotificationRequest;

public class OrganizationNotificationRequestFixture {

    public static OrganizationNotificationRequest create(
            Long deviceId
    ) {
        return new OrganizationNotificationRequest(
                deviceId
        );
    }
}
