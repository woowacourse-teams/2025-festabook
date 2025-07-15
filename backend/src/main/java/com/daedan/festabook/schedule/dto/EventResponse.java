package com.daedan.festabook.schedule.dto;

import com.daedan.festabook.schedule.domain.Event;
import com.daedan.festabook.schedule.domain.EventStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;

public record EventResponse(
        Long id,
        EventStatus status,
        @JsonFormat(pattern = "HH:mm") LocalTime startTime,
        @JsonFormat(pattern = "HH:mm") LocalTime endTime,
        String title,
        String location
) {

    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getId(),
                event.getStatus(),
                event.getStartTime(),
                event.getEndTime(),
                event.getTitle(),
                event.getLocation()
        );
    }
}
