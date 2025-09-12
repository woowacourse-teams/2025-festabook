package com.daedan.festabook.festival.domain;

import com.daedan.festabook.notification.dto.NotificationSendRequest;

public interface FestivalNotificationManager {

    void subscribeFestivalTopic(Long festivalId, String token);

    void unsubscribeFestivalTopic(Long festivalId, String token);

    void sendToFestivalTopic(Long festivalId, NotificationSendRequest request);
}
