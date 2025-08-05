package com.daedan.festabook.lostitem.dto;

import com.daedan.festabook.lostitem.domain.LostItem;
import com.daedan.festabook.lostitem.domain.PickupStatus;
import java.time.LocalDateTime;

public record LostItemResponse(
        Long id,
        String imageUrl,
        String storageLocation,
        PickupStatus status,
        LocalDateTime createdAt
) {

    public static LostItemResponse from(LostItem lostItem) {
        return new LostItemResponse(
                lostItem.getId(),
                lostItem.getImageUrl(),
                lostItem.getStorageLocation(),
                lostItem.getStatus(),
                lostItem.getCreatedAt()
        );
    }
}
