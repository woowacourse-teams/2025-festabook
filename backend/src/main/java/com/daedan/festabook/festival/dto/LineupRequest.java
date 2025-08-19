package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.Lineup;
import java.time.LocalDateTime;

public record LineupRequest(
        String name,
        String imageUrl,
        LocalDateTime performanceAt
) {

    public Lineup toEntity(Festival festival) {
        return new Lineup(
                festival,
                name,
                imageUrl,
                performanceAt
        );
    }
}
