package com.daedan.festabook.schedule.dto;

import com.daedan.festabook.schedule.domain.Event;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.Clock;
import java.util.List;

public record EventResponses(
        @JsonValue List<EventResponse> events
) {

    public static EventResponses from(List<Event> events, Clock clock) {
        return new EventResponses(
                events.stream()
                        .map(event -> EventResponse.from(event, clock))
                        .toList()
        );
    }
}
