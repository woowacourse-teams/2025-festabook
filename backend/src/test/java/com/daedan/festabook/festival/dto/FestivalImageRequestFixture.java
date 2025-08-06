package com.daedan.festabook.festival.dto;

public class FestivalImageRequestFixture {

    private static final String DEFAULT_IMAGE_URL = "http://example.com/...";

    public static FestivalImageRequest create() {
        return new FestivalImageRequest(
                DEFAULT_IMAGE_URL
        );
    }

    public static FestivalImageRequest create(
            String imageUrl
    ) {
        return new FestivalImageRequest(
                imageUrl
        );
    }
}
