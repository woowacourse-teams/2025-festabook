package com.daedan.festabook.festival.domain;

import com.daedan.festabook.festival.controller.LineupRequest;
import java.time.LocalDateTime;

public class LineupRequestFixture {

    private final static String DEFAULT_LINEUP_NAME = "이미소";
    private final static String DEFAULT_IMAGE_URL = "https://example.com/image.jpg";
    private final static LocalDateTime DEFAULT_PERFORM_AT = LocalDateTime.of(2025, 10, 15, 12, 0, 0);

    public static LineupRequest create() {
        return new LineupRequest(
                DEFAULT_LINEUP_NAME,
                DEFAULT_IMAGE_URL,
                DEFAULT_PERFORM_AT
        );
    }

    public static LineupRequest create(String name, String imageUrl, LocalDateTime performAt) {
        return new LineupRequest(
                name,
                imageUrl,
                performAt
        );
    }
}
