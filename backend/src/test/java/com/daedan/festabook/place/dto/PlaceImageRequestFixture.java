package com.daedan.festabook.place.dto;

public class PlaceImageRequestFixture {

    private static final String DEFAULT_IMAGE_URL = "https://example.com/images/1";

    public static PlaceImageRequest create() {
        return new PlaceImageRequest(
                DEFAULT_IMAGE_URL
        );
    }

    public static PlaceImageRequest create(
            String imageUrl
    ) {
        return new PlaceImageRequest(
                imageUrl
        );
    }
}
