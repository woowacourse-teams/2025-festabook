package com.daedan.festabook.event.dto;

import com.daedan.festabook.event.domain.EventDate;
import java.time.LocalDate;

public record EventDateResponse(
        Long id,
        LocalDate date
) {

    public static EventDateResponse from(EventDate eventDate) {
        return new EventDateResponse(
                eventDate.getId(),
                eventDate.getDate()
        );
    }
}
