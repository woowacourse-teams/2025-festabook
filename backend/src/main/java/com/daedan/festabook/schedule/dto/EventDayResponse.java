package com.daedan.festabook.schedule.dto;

import com.daedan.festabook.schedule.domain.EventDay;
import java.time.LocalDate;

public record EventDayResponse(
        Long id,
        LocalDate date
) {

    public static EventDayResponse from(EventDay eventDay) {
        return new EventDayResponse(
                eventDay.getId(),
                eventDay.getDate()
        );
    }
}
