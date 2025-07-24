package com.daedan.festabook.schedule.dto;

import com.daedan.festabook.schedule.domain.Event;
import com.daedan.festabook.schedule.domain.EventDate;
import java.time.LocalTime;

public record EventRequest(
        LocalTime startTime,
        LocalTime endTime,
        String title,
        String location,
        Long eventDateId
) {
    public Event toEntity(EventDate eventDate) {
        return new Event(
                startTime,
                endTime,
                title,
                location,
                eventDate
        );
    }
}
