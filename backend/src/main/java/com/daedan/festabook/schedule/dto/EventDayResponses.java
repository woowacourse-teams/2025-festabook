package com.daedan.festabook.schedule.dto;

import com.daedan.festabook.schedule.domain.EventDay;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record EventDayResponses(
        @JsonValue List<EventDayResponse> eventDays
) {

    public static EventDayResponses from(List<EventDay> eventDays) {
        return new EventDayResponses(
                eventDays.stream()
                        .map(EventDayResponse::from)
                        .toList()
        );
    }
}
