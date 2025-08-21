package com.daedan.festabook.lineup.domain;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.fixture.BaseEntityTestHelper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LineupFixture {

    private static final Festival DEFAULT_FESTIVAL = FestivalFixture.create();
    private static final String DEFAULT_LINEUP_NAME = "이미소";
    private static final String DEFAULT_IMAGE_URL = "https://example.com/image.jpg";
    private static final LocalDateTime DEFAULT_PERFORM_AT = LocalDateTime.of(2025, 10, 15, 12, 0, 0);

    public static Lineup create(
            Long lineupId
    ) {
        Lineup lineup = new Lineup(
                DEFAULT_FESTIVAL,
                DEFAULT_LINEUP_NAME,
                DEFAULT_IMAGE_URL,
                DEFAULT_PERFORM_AT
        );
        BaseEntityTestHelper.setId(lineup, lineupId);
        return lineup;
    }

    public static Lineup create(
            Festival festival
    ) {
        return new Lineup(
                festival,
                DEFAULT_LINEUP_NAME,
                DEFAULT_IMAGE_URL,
                DEFAULT_PERFORM_AT
        );
    }

    public static Lineup create(
            Festival festival,
            LocalDateTime dateTime
    ) {
        return new Lineup(
                festival,
                DEFAULT_LINEUP_NAME,
                DEFAULT_IMAGE_URL,
                dateTime
        );
    }

    public static Lineup create(
            Festival festival,
            String name,
            LocalDateTime dateTime
    ) {
        return new Lineup(
                festival,
                name,
                DEFAULT_IMAGE_URL,
                dateTime
        );
    }

    public static Lineup create(
            Long lineupId,
            Festival festival,
            String name,
            String imageUrl,
            LocalDateTime dateTime
    ) {
        Lineup lineup = new Lineup(
                festival,
                name,
                imageUrl,
                dateTime
        );
        BaseEntityTestHelper.setId(lineup, lineupId);
        return lineup;
    }

    public static List<Lineup> createList(int size, Festival festival) {
        return IntStream.range(0, size)
                .mapToObj(i -> LineupFixture.create(festival))
                .collect(Collectors.toList());
    }
}
