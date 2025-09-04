package com.daedan.festabook.lineup.dto;

import java.time.LocalDateTime;

public class LineupUpdateRequestFixture {

    private static final String DEFAULT_LINEUP_NAME = "김후유";
    private static final String DEFAULT_IMAGE_URL = "https://example.com/updated-image.jpg";
    private static final LocalDateTime DEFAULT_PERFORM_AT = LocalDateTime.of(2025, 10, 16, 15, 0, 0);

    public static LineupUpdateRequest create() {
        return new LineupUpdateRequest(
                DEFAULT_LINEUP_NAME,
                DEFAULT_IMAGE_URL,
                DEFAULT_PERFORM_AT
        );
    }

    public static LineupUpdateRequest create(
            String name,
            String imageUrl,
            LocalDateTime performAt
    ) {
        return new LineupUpdateRequest(
                name,
                imageUrl,
                performAt
        );
    }
}
