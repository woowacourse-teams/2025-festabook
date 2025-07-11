package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

public record PlaceRequest(

        @Schema(description = "제목", example = "코딩하며 한잔")
        String title,

        @Schema(description = "설명", example = "시원한 맥주와 맛있는 치킨!")
        String description,

        @Schema(description = "카테고리", example = "BAR")
        PlaceCategory category,

        @Schema(description = "장소", example = "공학관 앞")
        String location,

        @Schema(description = "주최", example = "C블C블")
        String host,

        @Schema(description = "운영 시작 시간", example = "09:00")
        LocalTime startTime,

        @Schema(description = "운영 마감 시간", example = "18:00")
        LocalTime endTime
) {
}
