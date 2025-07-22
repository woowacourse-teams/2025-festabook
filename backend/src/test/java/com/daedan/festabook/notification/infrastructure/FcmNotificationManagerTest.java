package com.daedan.festabook.notification.infrastructure;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.notification.dto.NotificationMessage;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FcmNotificationManagerTest {

    @Mock
    private FirebaseMessagingException firebaseMessagingException;

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @InjectMocks
    private FcmNotificationManager fcmNotificationManager;

    @Nested
    class subscribeOrganizationTopic {

        @Test
        void 예외_도메인_예외로_변화하여_던지기() throws FirebaseMessagingException {
            // given
            Long organizationId = 1L;
            String fcmToken = "fcmToken";
            willThrow(firebaseMessagingException)
                    .given(firebaseMessaging)
                    .subscribeToTopic(any(), any());

            // when & then
            assertThatThrownBy(() -> {
                fcmNotificationManager.subscribeOrganizationTopic(organizationId, fcmToken);
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("FCM 토픽 구독을 실패했습니다.");
        }
    }

    @Nested
    class unsubscribeOrganizationTopic {

        @Test
        void 예외_도메인_예외로_변화하여_던지기() throws FirebaseMessagingException {
            // given
            Long organizationId = 1L;
            String fcmToken = "fcmToken";
            willThrow(firebaseMessagingException)
                    .given(firebaseMessaging)
                    .unsubscribeFromTopic(any(), any());

            // when & then
            assertThatThrownBy(() -> {
                fcmNotificationManager.unsubscribeOrganizationTopic(organizationId, fcmToken);
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("FCM 토픽 구독 취소를 실패했습니다.");
        }
    }

    @Nested
    class sendToOrganizationTopic {

        @Test
        void 예외_도메인_예외로_변화하여_던지기() throws FirebaseMessagingException {
            // given
            Long organizationId = 1L;
            NotificationMessage notificationMessage = new NotificationMessage("title", "body");
            willThrow(firebaseMessagingException)
                    .given(firebaseMessaging)
                    .send(any());

            // when & then
            assertThatThrownBy(() -> {
                fcmNotificationManager.sendToOrganizationTopic(organizationId, notificationMessage);
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("FCM 메시지 전송을 실패했습니다.");
        }
    }

    @Nested
    class subscribePlaceTopic {

        @Test
        void 예외_도메인_예외로_변화하여_던지기() throws FirebaseMessagingException {
            // given
            Long placeId = 1L;
            String fcmToken = "fcmToken";
            willThrow(firebaseMessagingException)
                    .given(firebaseMessaging)
                    .subscribeToTopic(any(), any());

            // when & then
            assertThatThrownBy(() -> {
                fcmNotificationManager.subscribePlaceTopic(placeId, fcmToken);
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("FCM 토픽 구독을 실패했습니다.");
        }
    }

    @Nested
    class unsubscribePlaceTopic {

        @Test
        void 예외_도메인_예외로_변화하여_던지기() throws FirebaseMessagingException {
            // given
            Long placeId = 1L;
            String fcmToken = "fcmToken";
            willThrow(firebaseMessagingException)
                    .given(firebaseMessaging)
                    .unsubscribeFromTopic(any(), any());

            // when & then
            assertThatThrownBy(() -> {
                fcmNotificationManager.unsubscribePlaceTopic(placeId, fcmToken);
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("FCM 토픽 구독 취소를 실패했습니다.");
        }
    }
}
