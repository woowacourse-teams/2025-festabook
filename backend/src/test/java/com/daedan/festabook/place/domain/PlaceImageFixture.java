package com.daedan.festabook.place.domain;

import com.daedan.festabook.global.fixture.BaseEntityTestHelper;

public class PlaceImageFixture {

    private static final Place DEFAULT_PLACE = PlaceFixture.create();
    private static final String DEFAULT_IMAGE_URL = "https://example.com/image.jpg";
    private static final Integer DEFAULT_SEQUENCE = 3;

    public static PlaceImage create() {
        return new PlaceImage(
                DEFAULT_PLACE,
                DEFAULT_IMAGE_URL,
                DEFAULT_SEQUENCE
        );
    }

    public static PlaceImage create(
            Long id,
            Place place,
            int sequence
    ) {
        PlaceImage placeImage = new PlaceImage(
                place,
                DEFAULT_IMAGE_URL,
                sequence
        );
        BaseEntityTestHelper.setId(placeImage, id);
        return placeImage;
    }

    public static PlaceImage create(
            Place place
    ) {
        return new PlaceImage(
                place,
                DEFAULT_IMAGE_URL,
                DEFAULT_SEQUENCE
        );
    }

    public static PlaceImage create(
            Place place,
            Integer sequence
    ) {
        return new PlaceImage(
                place,
                DEFAULT_IMAGE_URL,
                sequence
        );
    }

    public static PlaceImage create(
            Long placeImageId,
            Integer sequence
    ) {
        PlaceImage placeImage = new PlaceImage(
                DEFAULT_PLACE,
                DEFAULT_IMAGE_URL,
                sequence
        );
        BaseEntityTestHelper.setId(placeImage, placeImageId);
        return placeImage;
    }

    public static PlaceImage create(
            Place place,
            String imageUrl,
            Integer sequence
    ) {
        return new PlaceImage(
                place,
                imageUrl,
                sequence
        );
    }
}
