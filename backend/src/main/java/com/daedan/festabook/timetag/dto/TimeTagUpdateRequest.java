package com.daedan.festabook.timetag.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TimeTagUpdateRequest(

        @Schema(description = "시간 태그 이름", example = "1일차 낮")
        String name
) {
}
