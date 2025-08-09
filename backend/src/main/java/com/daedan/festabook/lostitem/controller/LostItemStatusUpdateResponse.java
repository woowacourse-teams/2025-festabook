package com.daedan.festabook.lostitem.controller;

import com.daedan.festabook.lostitem.domain.LostItem;
import com.daedan.festabook.lostitem.domain.PickupStatus;

public record LostItemStatusUpdateResponse(
        Long lostItemId,
        PickupStatus status
) {

    public static LostItemStatusUpdateResponse from(LostItem lostItem) {
        return new LostItemStatusUpdateResponse(
                lostItem.getId(),
                lostItem.getStatus()
        );
    }
}
