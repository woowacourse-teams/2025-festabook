package com.daedan.festabook.festival.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record FestivalNotificationRequest(

        @Schema(description = "디바이스 ID", example = "1")
        Long deviceId
) {
}
