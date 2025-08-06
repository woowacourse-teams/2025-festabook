package com.daedan.festabook.lostitem.dto;

public class LostItemRequestFixture {

    private static final String DEFAULT_IMAGE_URL = "http://example.com/image.png";
    private static final String DEFAULT_STORAGE_LOCATION = "학생회실 305호";

    public static LostItemRequest create() {
        return new LostItemRequest(
                DEFAULT_IMAGE_URL,
                DEFAULT_STORAGE_LOCATION
        );
    }

    public static LostItemRequest create(
            String imageUrl,
            String storageLocation
    ) {
        return new LostItemRequest(
                imageUrl,
                storageLocation
        );
    }
}
