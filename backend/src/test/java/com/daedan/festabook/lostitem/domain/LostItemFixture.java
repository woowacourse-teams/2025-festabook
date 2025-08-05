package com.daedan.festabook.lostitem.domain;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;

public class LostItemFixture {

    private static final Organization DEFAULT_ORGANIZATION = OrganizationFixture.create();
    private static final String DEFAULT_IMAGE_URL = "http://example.com/image.png";
    private static final String DEFAULT_STORAGE_LOCATION = "창고A";
    private static final PickupStatus DEFAULT_PICK_UP_STATUS = PickupStatus.PENDING;

    public static LostItem create() {
        return new LostItem(
                DEFAULT_ORGANIZATION,
                DEFAULT_IMAGE_URL,
                DEFAULT_STORAGE_LOCATION,
                DEFAULT_PICK_UP_STATUS
        );
    }

    public static LostItem createWithImageUrl(
            String imageUrl
    ) {
        return new LostItem(
                DEFAULT_ORGANIZATION,
                imageUrl,
                DEFAULT_STORAGE_LOCATION,
                DEFAULT_PICK_UP_STATUS
        );
    }

    public static LostItem createWithStorageLocation(
            String storageLocation
    ) {
        return new LostItem(
                DEFAULT_ORGANIZATION,
                DEFAULT_IMAGE_URL,
                storageLocation,
                DEFAULT_PICK_UP_STATUS
        );
    }

    public static LostItem create(
            PickupStatus status
    ) {
        return new LostItem(
                DEFAULT_ORGANIZATION,
                DEFAULT_IMAGE_URL,
                DEFAULT_STORAGE_LOCATION,
                status
        );
    }

    public static LostItem create(
            String imageUrl,
            String storageLocation,
            PickupStatus status
    ) {
        return new LostItem(
                DEFAULT_ORGANIZATION,
                imageUrl,
                storageLocation,
                status
        );
    }

    public static LostItem create(
            Organization organization,
            String imageUrl,
            String storageLocation,
            PickupStatus status
    ) {
        return new LostItem(
                organization,
                imageUrl,
                storageLocation,
                status
        );
    }
}
