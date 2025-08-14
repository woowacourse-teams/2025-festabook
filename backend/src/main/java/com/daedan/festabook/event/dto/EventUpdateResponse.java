package com.daedan.festabook.event.dto;

import com.daedan.festabook.event.domain.Event;
import com.daedan.festabook.event.domain.EventStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Clock;
import java.time.LocalTime;

public record EventUpdateResponse(
        Long eventId,
        EventStatus status,
        @JsonFormat(pattern = "HH:mm") LocalTime startTime,
        @JsonFormat(pattern = "HH:mm") LocalTime endTime,
        String title,
        String location
) {

    public static EventUpdateResponse from(Event event, Clock clock) {
        return new EventUpdateResponse(
                event.getId(),
                event.determineStatus(clock),
                event.getStartTime(),
                event.getEndTime(),
                event.getTitle(),
                event.getLocation()
        );
    }
}
