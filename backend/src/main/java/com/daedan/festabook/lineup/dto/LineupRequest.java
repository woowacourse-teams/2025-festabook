package com.daedan.festabook.lineup.dto;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.lineup.domain.Lineup;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record LineupRequest(

        @Schema(description = "라인업 인물 이름", example = "이미소")
        String name,

        @Schema(description = "라인업 인물 이미지", example = "http://example.com/image.png")
        String imageUrl,

        @Schema(description = "라인업 일정", example = "2025-10-15T12:00:00")
        LocalDateTime performanceAt
) {

    public Lineup toEntity(Festival festival) {
        return new Lineup(
                festival,
                name,
                imageUrl,
                performanceAt
        );
    }
}
