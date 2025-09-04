package com.daedan.festabook.lostitem.domain;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.fixture.BaseEntityTestHelper;
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
            Festival festival,
            Long lostItemId
    ) {
        LostItem lostItem = new LostItem(
                festival,
                DEFAULT_IMAGE_URL,
                DEFAULT_STORAGE_LOCATION,
                DEFAULT_PICK_UP_STATUS
        );
        BaseEntityTestHelper.setId(lostItem, lostItemId);
        return BaseEntityTestHelper.setCreatedAt(lostItem, DEFAULT_CREATED_AT);
    }

    public static LostItem create(
            Festival festival,
            PickupStatus status,
            Long lostItemId
    ) {
        LostItem lostItem = new LostItem(
                festival,
                DEFAULT_IMAGE_URL,
                DEFAULT_STORAGE_LOCATION,
                status
        );
        BaseEntityTestHelper.setId(lostItem, lostItemId);
        return BaseEntityTestHelper.setCreatedAt(lostItem, DEFAULT_CREATED_AT);
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
            Long lostItemId
    ) {
        LostItem lostItem = new LostItem(
                festival,
                imageUrl,
                storageLocation,
                DEFAULT_PICK_UP_STATUS
        );
        BaseEntityTestHelper.setId(lostItem, lostItemId);
        return BaseEntityTestHelper.setCreatedAt(lostItem, DEFAULT_CREATED_AT);
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
}
