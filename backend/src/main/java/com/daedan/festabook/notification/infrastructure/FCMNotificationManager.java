package com.daedan.festabook.notification.infrastructure;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.notification.dto.NotificationMessage;
import com.daedan.festabook.organization.domain.OrganizationNotificationManager;
import com.daedan.festabook.place.domain.PlaceNotificationManager;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class FCMNotificationManager implements OrganizationNotificationManager, PlaceNotificationManager {

    private static final String ORGANIZATION_TOPIC_PREFIX = "notifications-organization-";
    private static final String PLACE_TOPIC_PREFIX = "notifications-place-";

    @Override
    public void subscribeOrganizationTopic(Long organizationId, String fcmToken) {
        String topic = ORGANIZATION_TOPIC_PREFIX + organizationId;
        subscribeTopic(topic, fcmToken);
    }

    @Override
    public void unsubscribeOrganizationTopic(Long organizationId, String fcmToken) {
        String topic = ORGANIZATION_TOPIC_PREFIX + organizationId;
        unsubscribeTopic(topic, fcmToken);
    }

    @Override
    public void sendToOrganizationTopic(Long organizationId, NotificationMessage notificationMessage) {
        String topic = ORGANIZATION_TOPIC_PREFIX + organizationId;
        sendToTopic(topic, notificationMessage);
    }

    @Override
    public void subscribePlaceTopic(Long placeId, String fcmToken) {
        String topic = PLACE_TOPIC_PREFIX + placeId;
        subscribeTopic(topic, fcmToken);
    }

    @Override
    public void unsubscribePlaceTopic(Long placeId, String fcmToken) {
        String topic = PLACE_TOPIC_PREFIX + placeId;
        unsubscribeTopic(topic, fcmToken);
    }

    protected void subscribeTopic(String topic, String fcmToken) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(List.of(fcmToken), topic);
        } catch (FirebaseMessagingException e) {
            throw new BusinessException("FCM 토픽 구독을 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected void unsubscribeTopic(String topic, String fcmToken) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(List.of(fcmToken), topic);
        } catch (FirebaseMessagingException e) {
            throw new BusinessException("FCM 토픽 구독 취소를 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected void sendToTopic(String topic, NotificationMessage notificationMessage) {
        Message message = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder()
                        .setTitle(notificationMessage.title())
                        .setBody(notificationMessage.body())
                        .build())
                .build();
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new BusinessException("FCM 메시지 전송을 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
