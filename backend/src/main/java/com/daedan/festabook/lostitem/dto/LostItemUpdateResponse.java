package com.daedan.festabook.lostitem.dto;

import com.daedan.festabook.lostitem.domain.LostItem;

public record LostItemUpdateResponse(
        String imageUrl,
        String storageLocation
) {

    public static LostItemUpdateResponse from(LostItem lostItem) {
        return new LostItemUpdateResponse(
                lostItem.getImageUrl(),
                lostItem.getStorageLocation()
        );
    }
}
