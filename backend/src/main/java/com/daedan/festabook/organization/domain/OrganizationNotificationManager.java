package com.daedan.festabook.organization.domain;

import com.daedan.festabook.notification.dto.NotificationMessage;

public interface OrganizationNotificationManager {

    void subscribeOrganizationTopic(Long organizationId, String token);

    void unsubscribeOrganizationTopic(Long organizationId, String token);

    void sendToOrganizationTopic(Long organizationId, NotificationMessage notificationMessage);
}
