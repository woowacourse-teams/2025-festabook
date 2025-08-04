package com.daedan.festabook.organization.dto;

import com.daedan.festabook.organization.domain.OrganizationNotification;

public record OrganizationNotificationResponse(
        Long id
) {

    public static OrganizationNotificationResponse from(OrganizationNotification organizationNotification) {
        return new OrganizationNotificationResponse(
                organizationNotification.getId()
        );
    }
}
