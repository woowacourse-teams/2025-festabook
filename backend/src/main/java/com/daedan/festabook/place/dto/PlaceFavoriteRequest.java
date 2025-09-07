package com.daedan.festabook.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceFavoriteRequest(

        @Schema(description = "디바이스 ID", example = "1")
        Long deviceId
) {
}
