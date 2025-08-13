package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

public record PlaceUpdateRequest(

        @Schema(description = "플레이스 카테고리", example = "BAR")
        PlaceCategory placeCategory,

        @Schema(description = "플레이스 이름", example = "음대의 음대음대 주점")
        String title,

        @Schema(description = "플레이스 설명", example = "음대 주점입니다.")
        String description,

        @Schema(description = "플레이스 위치", example = "대운동장 입구")
        String location,

        @Schema(description = "플레이스 호스트", example = "음대 1학년")
        String host,

        @Schema(description = "플레이스 운영 시작 시간", example = "12:30")
        LocalTime startTime,

        @Schema(description = "플레이스 운영 종료 시간", example = "23:50")
        LocalTime endTime
) {
}
