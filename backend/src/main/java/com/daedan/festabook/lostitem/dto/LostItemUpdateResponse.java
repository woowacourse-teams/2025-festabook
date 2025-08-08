package com.daedan.festabook.lostitem.dto;

import com.daedan.festabook.lostitem.domain.LostItem;

public record LostItemUpdateResponse(
        Long lostItemId,
        String imageUrl,
        String storageLocation
) {

    public static LostItemUpdateResponse from(LostItem lostItem) {
        return new LostItemUpdateResponse(
                lostItem.getId(),
                lostItem.getImageUrl(),
                lostItem.getStorageLocation()
        );
    }
}
