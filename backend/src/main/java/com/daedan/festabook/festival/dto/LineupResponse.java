package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.Lineup;

public record LineupResponse(
        Long lineupId,
        String name,
        String imageUrl
) {

    public static LineupResponse from(Lineup lineup) {
        return new LineupResponse(
                lineup.getId(),
                lineup.getName(),
                lineup.getImageUrl()
        );
    }
}
