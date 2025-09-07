package com.daedan.festabook.festival.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record FestivalInformationUpdateRequest(

        @Schema(description = "축제 이름", example = "2025 시립 Water Festival: AQUA WAVE")
        String festivalName,

        @Schema(description = "축제 시작 날짜", example = "2025-08-04")
        LocalDate startDate,

        @Schema(description = "축제 종료 날짜", example = "2025-08-07")
        LocalDate endDate
) {
}
