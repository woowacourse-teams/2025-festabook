package com.daedan.festabook.lostitem.domain;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import java.time.LocalDateTime;

public class LostItemFixture {

    private static final Festival DEFAULT_FESTIVAL = FestivalFixture.create();
    private static final String DEFAULT_IMAGE_URL = "http://example.com/image.png";
    private static final String DEFAULT_STORAGE_LOCATION = "창고A";
    private static final PickupStatus DEFAULT_PICK_UP_STATUS = PickupStatus.PENDING;
    private static final LocalDateTime DEFAULT_CREATED_AT = LocalDateTime.now();

    public static LostItem create() {
        return new LostItem(
                DEFAULT_FESTIVAL,
                DEFAULT_IMAGE_URL,
                DEFAULT_STORAGE_LOCATION,
                DEFAULT_PICK_UP_STATUS
        );
    }

    public static LostItem create(
            Festival festival
    ) {
        return new LostItem(
                festival,
                DEFAULT_IMAGE_URL,
                DEFAULT_STORAGE_LOCATION,
                DEFAULT_PICK_UP_STATUS
        );
    }

    public static LostItem createWithImageUrl(
            String imageUrl
    ) {
        return new LostItem(
                DEFAULT_FESTIVAL,
                imageUrl,
                DEFAULT_STORAGE_LOCATION,
                DEFAULT_PICK_UP_STATUS
        );
    }

    public static LostItem createWithStorageLocation(
            String storageLocation
    ) {
        return new LostItem(
                DEFAULT_FESTIVAL,
                DEFAULT_IMAGE_URL,
                storageLocation,
                DEFAULT_PICK_UP_STATUS
        );
    }

    public static LostItem create(
            PickupStatus status
    ) {
        return new LostItem(
                DEFAULT_FESTIVAL,
                DEFAULT_IMAGE_URL,
                DEFAULT_STORAGE_LOCATION,
                status
        );
    }

    public static LostItem create(
            Festival festival,
            PickupStatus status
    ) {
        return new LostItem(
                festival,
                DEFAULT_IMAGE_URL,
                DEFAULT_STORAGE_LOCATION,
                status
        );
    }

    public static LostItem create(
            Long lostItemId,
            PickupStatus status
    ) {
        return new LostItem(
                lostItemId,
                DEFAULT_FESTIVAL,
                DEFAULT_IMAGE_URL,
                DEFAULT_STORAGE_LOCATION,
                status,
                DEFAULT_CREATED_AT
        );
    }

    public static LostItem create(
            Long lostItemId,
            String imageUrl,
            String storageLocation
    ) {
        return new LostItem(
                lostItemId,
                DEFAULT_FESTIVAL,
                imageUrl,
                storageLocation,
                DEFAULT_PICK_UP_STATUS,
                DEFAULT_CREATED_AT
        );
    }

    public static LostItem create(
            String imageUrl,
            String storageLocation,
            PickupStatus status
    ) {
        return new LostItem(
                DEFAULT_FESTIVAL,
                imageUrl,
                storageLocation,
                status
        );
    }

    public static LostItem create(
            Festival festival,
            String imageUrl,
            String storageLocation,
            PickupStatus status
    ) {
        return new LostItem(
                festival,
                imageUrl,
                storageLocation,
                status
        );
    }
}
