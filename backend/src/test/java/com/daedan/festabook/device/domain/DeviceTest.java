package com.daedan.festabook.device.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.daedan.festabook.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DeviceTest {

    @Nested
    class validateDeviceIdentifier {

        @Test
        void 성공() {
            // given
            String deviceIdentifier = "device-abc123";

            // when & then
            assertThatCode(() -> DeviceFixture.createWithDeviceIdentifier(deviceIdentifier))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_디바이스_식별자_null() {
            // given
            String deviceIdentifier = null;

            // when & then
            assertThatThrownBy(() -> DeviceFixture.createWithDeviceIdentifier(deviceIdentifier))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("디바이스 식별자는 비어 있을 수 없습니다.");
        }

        @Test
        void 예외_디바이스_식별자_blank() {
            // given
            String deviceIdentifier = " ";

            // when & then
            assertThatThrownBy(() -> DeviceFixture.createWithDeviceIdentifier(deviceIdentifier))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("디바이스 식별자는 비어 있을 수 없습니다.");
        }
    }

    @Nested
    class validateFcmToken {

        @Test
        void 성공() {
            // given
            String fcmToken = "fcm-token-xyz";

            // when & then
            assertThatCode(() -> DeviceFixture.createWithFcmToken(fcmToken))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_FCM_토큰_null() {
            // given
            String fcmToken = null;

            // when & then
            assertThatThrownBy(() -> DeviceFixture.createWithFcmToken(fcmToken))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("FCM 토큰은 비어 있을 수 없습니다.");
        }

        @Test
        void 예외_FCM_토큰_blank() {
            // given
            String fcmToken = " ";

            // when & then
            assertThatThrownBy(() -> DeviceFixture.createWithFcmToken(fcmToken))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("FCM 토큰은 비어 있을 수 없습니다.");
        }
    }
}
