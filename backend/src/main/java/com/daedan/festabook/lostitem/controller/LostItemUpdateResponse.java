package com.daedan.festabook.lostitem.controller;

import com.daedan.festabook.lostitem.domain.LostItem;

public record LostItemUpdateResponse(
        String storageLocation,
        String imageUrl
) {

    public static LostItemUpdateResponse from(LostItem lostItem) {
        return new LostItemUpdateResponse(
                lostItem.getStorageLocation(),
                lostItem.getImageUrl()
        );
    }
}
