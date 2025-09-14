package com.daedan.festabook.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceExtraUpdateRequest(

        @Schema(description = "플레이스 이름", example = "음대의 음대음대 주점")
        String title
) {
}
