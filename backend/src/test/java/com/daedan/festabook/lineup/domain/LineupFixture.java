package com.daedan.festabook.lineup.domain;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.fixture.BaseEntityTestHelper;
import java.time.LocalDateTime;

public class LineupFixture {

    private static final Festival DEFAULT_FESTIVAL = FestivalFixture.create();
    private static final String DEFAULT_LINEUP_NAME = "이미소";
    private static final String DEFAULT_IMAGE_URL = "https://example.com/image.jpg";
    private static final LocalDateTime DEFAULT_PERFORMANCE_AT = LocalDateTime.of(2025, 10, 15, 12, 0, 0);

    public static Lineup create(
            Festival festival
    ) {
        return new Lineup(
                festival,
                DEFAULT_LINEUP_NAME,
                DEFAULT_IMAGE_URL,
                DEFAULT_PERFORMANCE_AT

        );
    }

    public static Lineup create(
            String name
    ) {
        return new Lineup(
                DEFAULT_FESTIVAL,
                name,
                DEFAULT_IMAGE_URL,
                DEFAULT_PERFORMANCE_AT

        );
    }

    public static Lineup create(
            Festival festival,
            Long lineupId
    ) {
        Lineup lineup = new Lineup(
                festival,
                DEFAULT_LINEUP_NAME,
                DEFAULT_IMAGE_URL,
                DEFAULT_PERFORMANCE_AT
        );
        BaseEntityTestHelper.setId(lineup, lineupId);
        return lineup;
    }

    public static Lineup create(
            Festival festival,
            Long lineupId,
            LocalDateTime performanceAt
    ) {
        Lineup lineup = new Lineup(
                festival,
                DEFAULT_LINEUP_NAME,
                DEFAULT_IMAGE_URL,
                performanceAt
        );
        BaseEntityTestHelper.setId(lineup, lineupId);
        return lineup;
    }

    public static Lineup create(
            Festival festival,
            LocalDateTime performanceAt
    ) {
        return new Lineup(
                festival,
                DEFAULT_LINEUP_NAME,
                DEFAULT_IMAGE_URL,
                performanceAt
        );
    }

    public static Lineup create(
            Festival festival,
            String name,
            LocalDateTime performanceAt
    ) {
        return new Lineup(
                festival,
                name,
                DEFAULT_IMAGE_URL,
                performanceAt
        );
    }

    public static Lineup create(
            Festival festival,
            String name,
            String imageUrl,
            LocalDateTime performanceAt,
            Long lineupId
    ) {
        Lineup lineup = new Lineup(
                festival,
                name,
                imageUrl,
                performanceAt
        );
        BaseEntityTestHelper.setId(lineup, lineupId);
        return lineup;
    }
}
