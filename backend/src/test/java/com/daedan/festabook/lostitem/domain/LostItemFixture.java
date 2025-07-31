package com.daedan.festabook.lostitem.domain;

public class LostItemFixture {

    private static final String DEFAULT_IMAGE_URL = "http://example.com/image.png";
    private static final String DEFAULT_STORAGE_LOCATION = "창고A";
    private static final ClaimStatus DEFAULT_CLAIM_STATUS = ClaimStatus.UNCLAIMED;

    public static LostItem createWithImageUrl(
            String imageUrl
    ) {
        return new LostItem(
                imageUrl,
                DEFAULT_STORAGE_LOCATION,
                DEFAULT_CLAIM_STATUS
        );
    }

    public static LostItem createWithStorageLocation(
            String storageLocation
    ) {
        return new LostItem(
                DEFAULT_IMAGE_URL,
                storageLocation,
                DEFAULT_CLAIM_STATUS
        );
    }

    public static LostItem createWithClaimStatus(
            ClaimStatus claimStatus
    ) {
        return new LostItem(
                DEFAULT_IMAGE_URL,
                DEFAULT_STORAGE_LOCATION,
                claimStatus
        );
    }
}
