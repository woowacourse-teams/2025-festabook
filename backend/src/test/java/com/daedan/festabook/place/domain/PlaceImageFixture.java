package com.daedan.festabook.place.domain;

public class PlaceImageFixture {

    private static final Place DEFAULT_PLACE = PlaceFixture.create();
    private static final String DEFAULT_IMAGE_URL = "https://example.com/image.jpg";

    public static PlaceImage create() {
        return new PlaceImage(
                DEFAULT_PLACE,
                DEFAULT_IMAGE_URL
        );
    }

    public static PlaceImage create(
            Place place,
            String imageUrl
    ) {
        return new PlaceImage(place, imageUrl);
    }
}
