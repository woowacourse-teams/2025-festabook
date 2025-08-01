package com.daedan.festabook.lostitem.domain;

public class LostItemFixture {

    private static final String DEFAULT_IMAGE_URL = "http://example.com/image.png";
    private static final String DEFAULT_STORAGE_LOCATION = "창고A";
    private static final PickupStatus DEFAULT_PICK_UP_STATUS = PickupStatus.PENDING;

    public static LostItem createWithImageUrl(
            String imageUrl
    ) {
        return new LostItem(
                imageUrl,
                DEFAULT_STORAGE_LOCATION,
                DEFAULT_PICK_UP_STATUS
        );
    }

    public static LostItem createWithStorageLocation(
            String storageLocation
    ) {
        return new LostItem(
                DEFAULT_IMAGE_URL,
                storageLocation,
                DEFAULT_PICK_UP_STATUS
        );
    }

    public static LostItem createWithPickupStatus(
            PickupStatus pickupStatus
    ) {
        return new LostItem(
                DEFAULT_IMAGE_URL,
                DEFAULT_STORAGE_LOCATION,
                pickupStatus
        );
    }

    public static LostItem create(
            String imageUrl,
            String storageLocation,
            PickupStatus pickupStatus
    ) {
        return new LostItem(
                imageUrl,
                storageLocation,
                pickupStatus
        );
    }
}
