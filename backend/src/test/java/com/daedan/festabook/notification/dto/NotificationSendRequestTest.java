package com.daedan.festabook.notification.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.daedan.festabook.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class NotificationSendRequestTest {

    @Nested
    class constructor {

        @Test
        void 성공_빌더를_통해_객체_생성() {
            // given
            String title = "title";
            String body = "body";
            String customData = "customData";

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
            });
        }
    }

    @Nested
    class validateTitle {

        @ParameterizedTest(name = "제목: {0}")
        @NullAndEmptySource
        void 예외(String title) {
            // when & then
            assertThatThrownBy(() -> {
                NotificationSendRequest.builder()
                        .title(title)
                        .body("body")
                        .build();
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("알림 제목은 비어있을 수 없습니다.");
        }
    }

    @Nested
    class validateBody {

        @ParameterizedTest(name = "본문: {0}")
        @NullAndEmptySource
        void 예외(String body) {
            // when & then
            assertThatThrownBy(() -> {
                NotificationSendRequest.builder()
                        .title("title")
                        .body(body)
                        .build();
            })
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("알림 본문은 비어있을 수 없습니다.");
        }
    }

    @Nested
    class getCustomData {

        @Test
        void 성공_key_조회() {
            // given
            String key = "key";
            String value = "customData";
            NotificationSendRequest request = NotificationSendRequest.builder()
                    .title("title")
                    .body("body")
                    .putData(key, value)
                    .build();

            // when
            String result = request.getCustomData(key);

            // then
            assertThat(result).isEqualTo(value);
        }

        @Test
        void 예외_존재하지_않는_key로_조회() {
            // given
            NotificationSendRequest request = NotificationSendRequest.builder()
                    .title("title")
                    .body("body")
                    .putData("key", "customData")
                    .build();

            String notExistKey = "notExistKey";

            // when, then
            assertThatThrownBy(() -> request.getCustomData(notExistKey))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Key: [%s]에 해당하는 값이 존재하지 않습니다.", notExistKey);
        }
    }
}
