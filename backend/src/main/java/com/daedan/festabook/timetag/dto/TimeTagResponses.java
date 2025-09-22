package com.daedan.festabook.timetag.dto;

import com.daedan.festabook.timetag.domain.TimeTag;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record TimeTagResponses(
        @JsonValue List<TimeTagResponse> responses
) {

    public static TimeTagResponses from(List<TimeTag> timeTags) {
        return new TimeTagResponses(
                timeTags.stream()
                        .map(TimeTagResponse::from)
                        .toList()
        );
    }
}
