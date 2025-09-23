package com.daedan.festabook.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record EtcPlaceUpdateRequest(

        @Schema(description = "플레이스 이름", example = "음대의 음대음대 주점")
        String title,

        @Schema(description = "시간 태그 목록", example = "[1, 2, 3]")
        List<Long> timeTags
) {
}
