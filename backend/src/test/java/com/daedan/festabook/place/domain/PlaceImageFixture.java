package com.daedan.festabook.place.domain;

public class PlaceImageFixture {

    private static final Place DEFAULT_PLACE = PlaceFixture.create();
    private static final String DEFAULT_IMAGE_URL = "https://example.com/image.jpg";
    private static final Integer DEFAULT_ORDER = 3;

    public static PlaceImage create() {
        return new PlaceImage(
                DEFAULT_PLACE,
                DEFAULT_IMAGE_URL,
                DEFAULT_ORDER
        );
    }

    public static PlaceImage create(
            Place place
    ) {
        return new PlaceImage(
                place,
                DEFAULT_IMAGE_URL,
                DEFAULT_ORDER
        );
    }

    public static PlaceImage create(
            Place place,
            String imageUrl,
            Integer order
    ) {
        return new PlaceImage(place, imageUrl, order);
    }
}
