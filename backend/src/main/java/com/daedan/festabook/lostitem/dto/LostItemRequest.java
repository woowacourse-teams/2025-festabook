package com.daedan.festabook.lostitem.dto;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.lostitem.domain.LostItem;
import com.daedan.festabook.lostitem.domain.PickupStatus;

public record LostItemRequest(
        String imageUrl,
        String storageLocation
) {

    public LostItem toLostItem(Festival festival) {
        return new LostItem(
                festival,
                imageUrl,
                storageLocation,
                PickupStatus.PENDING
        );
    }
}
