package com.daedan.festabook.event.dto;

import com.daedan.festabook.event.domain.EventDate;
import com.daedan.festabook.festival.domain.Festival;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record EventDateUpdateRequest(

        @Schema(description = "일정 날짜", example = "2025-07-18")
        LocalDate date
) {

    public EventDate toEntity(Festival festival) {
        return new EventDate(
                festival,
                date
        );
    }
}
