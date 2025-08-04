package com.daedan.festabook.event.dto;

import com.daedan.festabook.event.domain.EventDate;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record EventDateResponses(
        @JsonValue List<EventDateResponse> responses
) {

    public static EventDateResponses from(List<EventDate> eventDates) {
        return new EventDateResponses(
                eventDates.stream()
                        .map(EventDateResponse::from)
                        .toList()
        );
    }
}
