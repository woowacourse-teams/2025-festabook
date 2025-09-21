package com.daedan.festabook.notification.infrastructure;

import com.daedan.festabook.festival.domain.FestivalNotificationManager;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.global.logging.Loggable;
import com.daedan.festabook.notification.dto.NotificationSendRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Loggable
@Component
@RequiredArgsConstructor
public class FcmNotificationManager implements FestivalNotificationManager {

    private static final String ANDROID_TOPIC_SUFFIX = "-android";
    private static final String IOS_TOPIC_SUFFIX = "-ios";

    @Value("${fcm.topic.festival-prefix}")
    private String topicFestivalPrefix;

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void subscribeAndroidFestivalTopic(Long festivalId, String token) {
        String topic = buildAndroidFestivalTopic(festivalId);
        subscribeTopic(topic, token);
    }

    @Override
    public void subscribeIosFestivalTopic(Long festivalId, String token) {
        String topic = buildIosFestivalTopic(festivalId);
        subscribeTopic(topic, token);
    }

    @Override
    public void subscribeFestivalTopic(Long festivalId, String fcmToken) {
        String topic = buildNoneSuffixFestivalTopic(festivalId);
        subscribeTopic(topic, fcmToken);
    }

    // TODO: 항상 3번의 외부 api 요청을 보내는 작업 리팩터링 하기
    @Override
    public void unsubscribeFestivalTopic(Long festivalId, String fcmToken) {
        String androidTopic = buildAndroidFestivalTopic(festivalId);
        unsubscribeTopic(androidTopic, fcmToken);

        String iosTopic = buildIosFestivalTopic(festivalId);
        unsubscribeTopic(iosTopic, fcmToken);

        String topic = buildNoneSuffixFestivalTopic(festivalId);
        unsubscribeTopic(topic, fcmToken);
    }

    // TODO: 항상 3번의 외부 api 요청을 보내는 작업 리팩터링 하기
    @Override
    public void sendToFestivalTopic(Long festivalId, NotificationSendRequest request) {
        String androidTopic = buildAndroidFestivalTopic(festivalId);
        sendDataMessageToTopic(androidTopic, festivalId, request);

        String iosTopic = buildIosFestivalTopic(festivalId);
        sendNotificationMessageToTopic(iosTopic, festivalId, request);

        String noneSuffixTopic = buildNoneSuffixFestivalTopic(festivalId);
        sendDataMessageToTopic(noneSuffixTopic, festivalId, request);
    }

    private String buildAndroidFestivalTopic(Long festivalId) {
        return topicFestivalPrefix + festivalId + ANDROID_TOPIC_SUFFIX;
    }

    private String buildIosFestivalTopic(Long festivalId) {
        return topicFestivalPrefix + festivalId + IOS_TOPIC_SUFFIX;
    }

    private String buildNoneSuffixFestivalTopic(Long festivalId) {
        return topicFestivalPrefix + festivalId;
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

    private void sendDataMessageToTopic(String topic, Long topicTargetId, NotificationSendRequest request) {
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

    private void sendNotificationMessageToTopic(String topic, Long topicTargetId, NotificationSendRequest request) {
        Message message = Message.builder()
                .setTopic(topic)

                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getBody())
                        .build())

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
