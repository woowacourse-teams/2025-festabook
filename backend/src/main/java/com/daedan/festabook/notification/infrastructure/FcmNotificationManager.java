package com.daedan.festabook.notification.infrastructure;

import com.daedan.festabook.festival.domain.FestivalNotificationManager;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.notification.dto.NotificationSendRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FcmNotificationManager implements FestivalNotificationManager {

    @Value("${fcm.topic.prefix}")
    private String topicPrefix;

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void subscribeFestivalTopic(Long festivalId, String fcmToken) {
        String topic = topicPrefix + festivalId;
        subscribeTopic(topic, fcmToken);
    }

    @Override
    public void unsubscribeFestivalTopic(Long festivalId, String fcmToken) {
        String topic = topicPrefix + festivalId;
        unsubscribeTopic(topic, fcmToken);
    }

    @Override
    public void sendToFestivalTopic(Long festivalId, NotificationSendRequest request) {
        sendToTopic(topicPrefix, festivalId, request);
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

    private void sendToTopic(String topicNamePrefix, Long topicTargetId, NotificationSendRequest request) {
        String topic = topicNamePrefix + topicTargetId;

        Message message = Message.builder()
                .setTopic(topic)

                .putData("title", request.getTitle())
                .putData("body", request.getBody())

                .putData("festivalId", String.valueOf(topicTargetId))
                .putData("announcementId", request.getCustomData("announcementId"))
                .build();

        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            throw new BusinessException("FCM 메시지 전송을 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
