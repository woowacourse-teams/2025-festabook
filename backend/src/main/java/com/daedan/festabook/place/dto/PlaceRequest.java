package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

public record PlaceRequest(

        @Schema(description = "제목", example = "")
        String title,
        String description,
        PlaceCategory category,
        String location,
        String host,
        LocalTime startTime,
        LocalTime endTime
) {
}
