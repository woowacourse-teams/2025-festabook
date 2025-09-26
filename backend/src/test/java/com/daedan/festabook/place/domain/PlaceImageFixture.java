package com.daedan.festabook.place.domain;

import com.daedan.festabook.global.fixture.BaseEntityTestHelper;

public class PlaceImageFixture {

    private static final String DEFAULT_IMAGE_URL = "https://example.com/image.jpg";
    private static final Integer DEFAULT_SEQUENCE = 3;

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
            String imageUrl,
            int sequence
    ) {
        return new PlaceImage(
                place,
                imageUrl,
                sequence
        );
    }

    public static PlaceImage create(
            Place place,
            Long placeImageId
    ) {
        PlaceImage placeImage = new PlaceImage(
                place,
                DEFAULT_IMAGE_URL,
                DEFAULT_SEQUENCE
        );
        BaseEntityTestHelper.setId(placeImage, placeImageId);
        return placeImage;
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
            Place place,
            int sequence,
            Long placeImageId
    ) {
        PlaceImage placeImage = new PlaceImage(
                place,
                DEFAULT_IMAGE_URL,
                sequence
        );
        BaseEntityTestHelper.setId(placeImage, placeImageId);
        return placeImage;
    }
}
