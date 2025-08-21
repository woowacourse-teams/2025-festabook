package com.daedan.festabook.lineup.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record LineupUpdateRequest(

        @Schema(description = "라인업 인물 이름", example = "이미소")
        String name,

        @Schema(description = "라인업 인물 이미지", example = "http://example.com/image.png")
        String imageUrl,

        @Schema(description = "라인업 일정", example = "2025-10-15T12:00:00")
        LocalDateTime performanceAt
) {
}