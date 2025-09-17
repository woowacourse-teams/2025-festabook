package com.daedan.festabook.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record DeviceUpdateRequest(

        @Schema(description = "FCM 토큰", example = "e4Jse...")
        String fcmToken
) {
}
