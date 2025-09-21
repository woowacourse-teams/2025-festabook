package com.daedan.festabook.timetag.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record TimeTagNameResponses(
        @JsonValue List<String> timeTagNames
) {
}
