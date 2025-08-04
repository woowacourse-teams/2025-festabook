package com.daedan.festabook.event.dto;

import com.daedan.festabook.event.domain.Event;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.Clock;
import java.util.List;

public record EventResponses(
        @JsonValue List<EventResponse> responses
) {

    public static EventResponses from(List<Event> events, Clock clock) {
        return new EventResponses(
                events.stream()
                        .map(event -> EventResponse.from(event, clock))
                        .toList()
        );
    }
}
