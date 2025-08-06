package com.daedan.festabook.notification.infrastructure;

import com.daedan.festabook.festival.domain.FestivalNotificationManager;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.notification.dto.NotificationMessage;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FcmNotificationManager implements FestivalNotificationManager {

    private static final String FESTIVAL_PREFIX = "festival";

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void subscribeFestivalTopic(Long festivalId, String fcmToken) {
        String topic = FESTIVAL_PREFIX + festivalId;
        subscribeTopic(topic, fcmToken);
    }

    @Override
    public void unsubscribeFestivalTopic(Long festivalId, String fcmToken) {
        String topic = FESTIVAL_PREFIX + festivalId;
        unsubscribeTopic(topic, fcmToken);
    }

    @Override
    public void sendToFestivalTopic(Long festivalId, NotificationMessage notificationMessage) {
        sendToTopic(FESTIVAL_PREFIX, festivalId, notificationMessage);
    }

    private void subscribeTopic(String topic, String fcmToken) {
        try {
            firebaseMessaging.subscribeToTopic(List.of(fcmToken), topic);
        } catch (FirebaseMessagingException e) {
            throw new BusinessException("FCM 토픽 구독을 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void unsubscribeTopic(String topic, String fcmToken) {
        try {
            firebaseMessaging.unsubscribeFromTopic(List.of(fcmToken), topic);
        } catch (FirebaseMessagingException e) {
            throw new BusinessException("FCM 토픽 구독 취소를 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void sendToTopic(String prefix, Long id, NotificationMessage notificationMessage) {
        String topic = prefix + id;

        Message message = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder()
                        .setTitle(notificationMessage.title())
                        .setBody(notificationMessage.body())
                        .build())
                .putData("type", prefix)
                .putData("id", String.valueOf(id))
                .build();

        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            throw new BusinessException("FCM 메시지 전송을 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
