package com.daedan.festabook.notification.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.notification.dto.NotificationRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void subscribeTopic(String fcmToken, String topic) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(List.of(fcmToken), topic);
        } catch (FirebaseMessagingException e) {
            throw new BusinessException("FCM 토픽 구독을 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void unsubscribeTopic(String fcmToken, String topic) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(List.of(fcmToken), topic);
        } catch (FirebaseMessagingException e) {
            throw new BusinessException("FCM 토픽 구독 취소를 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void sendToTopic(NotificationRequest request) {
        Message message = Message.builder()
                .setTopic(request.topic())
                .setNotification(Notification.builder()
                        .setTitle(request.title())
                        .setBody(request.body())
                        .build())
                .build();
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new BusinessException("FCM 메시지 전송을 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
