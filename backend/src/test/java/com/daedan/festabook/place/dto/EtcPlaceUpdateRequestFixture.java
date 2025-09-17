package com.daedan.festabook.place.dto;

public class EtcPlaceUpdateRequestFixture {

    private static final String DEFAULT_TITLE = "수정된 이름";

    public static EtcPlaceUpdateRequest create() {
        return new EtcPlaceUpdateRequest(
                DEFAULT_TITLE
        );
    }

    public static EtcPlaceUpdateRequest create(
            String title
    ) {
        return new EtcPlaceUpdateRequest(
                title
        );
    }
}
