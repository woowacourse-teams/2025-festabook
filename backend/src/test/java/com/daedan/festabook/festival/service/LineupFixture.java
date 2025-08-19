package com.daedan.festabook.festival.service;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.Lineup;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LineupFixture {

    private static final String DEFAULT_LINEUP_NAME = "이미소";
    private static final String DEFAULT_IMAGE = "https://example.com/image.jpg";
    private static final LocalDateTime DEFAULT_PERFORM_AT = LocalDateTime.of(2025, 10, 15, 12, 0, 0);

    public static Lineup create(
            Festival festival
    ) {
        return new Lineup(
                festival,
                DEFAULT_LINEUP_NAME,
                DEFAULT_IMAGE,
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
                DEFAULT_IMAGE,
                dateTime
        );
    }

    public static List<Lineup> createList(int size, Festival festival) {
        return IntStream.range(0, size)
                .mapToObj(i -> LineupFixture.create(festival))
                .collect(Collectors.toList());
    }
}
