package com.daedan.festabook.device.dto;

import com.daedan.festabook.device.domain.Device;
import io.swagger.v3.oas.annotations.media.Schema;

public record DeviceRequest(

        @Schema(description = "FCM 토큰", example = "e4Jse...")
        String fcmToken
) {

    public Device toEntity() {
        return new Device(
                fcmToken
        );
    }
}
