package com.daedan.festabook.event.dto;

import com.daedan.festabook.event.domain.EventDate;
import java.time.LocalDate;

public record EventDateUpdateResponse(
        Long eventDateId,
        LocalDate date
) {

    public static EventDateUpdateResponse from(EventDate eventDate) {
        return new EventDateUpdateResponse(
                eventDate.getId(),
                eventDate.getDate()
        );
    }
}
