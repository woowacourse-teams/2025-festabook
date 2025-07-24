package com.daedan.festabook.schedule.dto;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.schedule.domain.EventDate;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record EventDateRequest(
        @Schema(description = "일정 날짜", example = "2025-07-18")
        LocalDate date
) {

    public EventDate toEntity(Organization organization) {
        return new EventDate(
                organization,
                date
        );
    }
}
