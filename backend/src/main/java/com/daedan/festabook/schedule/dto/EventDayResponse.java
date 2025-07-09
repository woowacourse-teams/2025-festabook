package com.daedan.festabook.schedule.dto;

import com.daedan.festabook.schedule.domain.EventDay;
import java.time.LocalDate;

public record EventDayResponse(
        Long id,
        LocalDate date
) {

    public EventDayResponse(EventDay eventDay) {
        this(
                eventDay.getId(),
                eventDay.getDate()
        );
    }
}
