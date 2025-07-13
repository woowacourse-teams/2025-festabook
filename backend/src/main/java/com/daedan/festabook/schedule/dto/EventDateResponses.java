package com.daedan.festabook.schedule.dto;

import com.daedan.festabook.schedule.domain.EventDate;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record EventDateResponses(
        @JsonValue List<EventDateResponse> eventDate
) {

    public static EventDateResponses from(List<EventDate> eventDates) {
        return new EventDateResponses(
                eventDates.stream()
                        .map(EventDateResponse::from)
                        .toList()
        );
    }
}
