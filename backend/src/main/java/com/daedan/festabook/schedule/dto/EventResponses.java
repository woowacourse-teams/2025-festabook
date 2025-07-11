package com.daedan.festabook.schedule.dto;

import com.daedan.festabook.schedule.domain.Event;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record EventResponses(
        @JsonValue List<EventResponse> events
) {

    public static EventResponses from(List<Event> events) {
        return new EventResponses(
                events.stream()
                        .map(EventResponse::from)
                        .toList()
        );
    }
}
