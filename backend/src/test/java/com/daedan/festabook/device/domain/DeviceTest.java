package com.daedan.festabook.device.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DeviceTest {

    private final String DEFAULT_DEVICE_IDENTIFIER = "device-abc123";
    private final String DEFAULT_FCM_TOKEN = "fcm-token-xyz";

    @Nested
    class validateDeviceIdentifier {

        @Test
        void 성공() {
            // given
            String identifier = "device-abc123";

            // when & then
            assertThatCode(() -> new Device(identifier, DEFAULT_FCM_TOKEN))
                    .doesNotThrowAnyException();
        }

        @Test
        void 실패_디바이스_식별자_null() {
            // given
            String identifier = null;

            // when & then
            assertThatThrownBy(() -> new Device(identifier, DEFAULT_FCM_TOKEN))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("디바이스 식별자는 비어 있을 수 없습니다.");
        }

        @Test
        void 실패_디바이스_식별자_blank() {
            // given
            String identifier = " ";

            // when & then
            assertThatThrownBy(() -> new Device(identifier, DEFAULT_FCM_TOKEN))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("디바이스 식별자는 비어 있을 수 없습니다.");
        }
    }

    @Nested
    class validateFcmToken {

        @Test
        void 성공_FCM_토큰_정상값() {
            // given
            String token = "fcm-token-xyz";

            // when & then
            assertThatCode(() -> new Device(DEFAULT_DEVICE_IDENTIFIER, token))
                    .doesNotThrowAnyException();
        }

        @Test
        void 성공_FCM_토큰_null_허용() {
            // given
            String token = null;

            // when & then
            assertThatCode(() -> new Device(DEFAULT_DEVICE_IDENTIFIER, token))
                    .doesNotThrowAnyException();
        }

        @Test
        void 실패_FCM_토큰_blank() {
            // given
            String token = " ";

            // when & then
            assertThatThrownBy(() -> new Device(DEFAULT_DEVICE_IDENTIFIER, token))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("FCM 토큰은 null(권한 거부일 경우)이거나 유효한 값이어야 합니다.");
        }
    }
}
