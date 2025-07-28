package com.daedan.festabook.device.domain;

import com.daedan.festabook.global.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String deviceIdentifier;

    private String fcmToken;

    protected Device(
            Long id,
            String deviceIdentifier,
            String fcmToken
    ) {
        validateDeviceIdentifier(deviceIdentifier);
        validateFcmToken(fcmToken);

        this.id = id;
        this.deviceIdentifier = deviceIdentifier;
        this.fcmToken = fcmToken;
    }

    public Device(
            String deviceIdentifier,
            String fcmToken
    ) {
        this(
                null,
                deviceIdentifier,
                fcmToken
        );
    }

    private void validateDeviceIdentifier(String deviceIdentifier) {
        if (deviceIdentifier == null || deviceIdentifier.trim().isEmpty()) {
            throw new BusinessException("디바이스 식별자는 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateFcmToken(String fcmToken) {
        if (fcmToken != null && fcmToken.trim().isEmpty()) {
            throw new BusinessException("FCM 토큰은 null(권한 거부일 경우)이거나 유효한 값이어야 합니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
