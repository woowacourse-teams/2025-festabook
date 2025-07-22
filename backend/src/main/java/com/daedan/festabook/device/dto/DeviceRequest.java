package com.daedan.festabook.device.dto;

import com.daedan.festabook.device.domain.Device;
import io.swagger.v3.oas.annotations.media.Schema;

public record DeviceRequest(

        @Schema(description = "기기 식별자", example = "f47ac10b...")
        String deviceIdentifier,

        @Schema(description = "FCM 토큰", example = "e4Jse...")
        String fcmToken
) {

    public Device toEntity() {
        return new Device(
                deviceIdentifier,
                fcmToken
        );
    }
}
