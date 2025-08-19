package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.Lineup;
import java.time.LocalDateTime;

public record LineupResponse(
        Long lineupId,
        String name,
        String imageUrl,
        LocalDateTime performanceAt
) {

    public static LineupResponse from(Lineup lineup) {
        return new LineupResponse(
                lineup.getId(),
                lineup.getName(),
                lineup.getImageUrl(),
                lineup.getPerformanceAt()
        );
    }
}
