package com.daedan.festabook.notification.dto;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class NotificationSendRequestTest {

    @Nested
    class constructor {

        @Test
        void 성공_빌더를_통해_객체_생성() {
            // given
            String title = "title";
            String body = "body";
            String customData = "customData";
            String expectedNotExistCustomData = "";

            // when
            NotificationSendRequest request = NotificationSendRequest.builder()
                    .title(title)
                    .body(body)
                    .putData("key", customData)
                    .build();

            // then
            assertSoftly(s -> {
                s.assertThat(request.getTitle()).isEqualTo(title);
                s.assertThat(request.getBody()).isEqualTo(body);
                s.assertThat(request.getCustomData("key")).isEqualTo(customData);
                s.assertThat(request.getCustomData("notExistKey")).isEqualTo(expectedNotExistCustomData);
            });
        }
    }
}
