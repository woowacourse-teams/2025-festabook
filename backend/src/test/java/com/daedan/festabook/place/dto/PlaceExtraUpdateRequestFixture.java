package com.daedan.festabook.place.dto;

public class PlaceExtraUpdateRequestFixture {

    private static final String DEFAULT_TITLE = "수정된 이름";

    public static PlaceExtraUpdateRequest create() {
        return new PlaceExtraUpdateRequest(
                DEFAULT_TITLE
        );
    }

    public static PlaceExtraUpdateRequest create(
            String title
    ) {
        return new PlaceExtraUpdateRequest(
                title
        );
    }
}
