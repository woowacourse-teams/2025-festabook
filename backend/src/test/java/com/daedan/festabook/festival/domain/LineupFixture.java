package com.daedan.festabook.festival.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LineupFixture {

    private static final String DEFAULT_LINEUP_NAME = "이미소";
    private static final String DEFAULT_IMAGE_URL = "https://example.com/image.jpg";
    private static final LocalDateTime DEFAULT_PERFORM_AT = LocalDateTime.of(2025, 10, 15, 12, 0, 0);

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
        return new Lineup(
                lineupId,
                festival,
                name,
                imageUrl,
                dateTime
        );
    }

    public static List<Lineup> createList(int size, Festival festival) {
        return IntStream.range(0, size)
                .mapToObj(i -> LineupFixture.create(festival))
                .collect(Collectors.toList());
    }
}
