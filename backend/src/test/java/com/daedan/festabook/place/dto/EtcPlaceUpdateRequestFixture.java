package com.daedan.festabook.place.dto;

import java.util.List;

public class EtcPlaceUpdateRequestFixture {

    private static final String DEFAULT_TITLE = "수정된 이름";
    private static final List<Long> DEFAULT_TIME_TAGS = List.of();

    public static EtcPlaceUpdateRequest create() {
        return new EtcPlaceUpdateRequest(
                DEFAULT_TITLE,
                DEFAULT_TIME_TAGS
        );
    }

    public static EtcPlaceUpdateRequest create(
            String title
    ) {
        return new EtcPlaceUpdateRequest(
                title,
                DEFAULT_TIME_TAGS
        );
    }

    public static EtcPlaceUpdateRequest create(
            String title,
            List<Long> timeTags
    ) {
        return new EtcPlaceUpdateRequest(
                title,
                timeTags
        );
    }
}
