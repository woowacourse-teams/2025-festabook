package com.daedan.festabook.lineup.dto;

import com.daedan.festabook.lineup.domain.Lineup;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record LineupResponses(
        @JsonValue List<LineupResponse> responses
) {

    public static LineupResponses from(List<Lineup> lineups) {
        return new LineupResponses(
                lineups.stream()
                        .map(LineupResponse::from)
                        .toList()
        );
    }
}
