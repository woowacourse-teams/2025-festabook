package com.daedan.festabook.lostitem.dto;

import com.daedan.festabook.lostitem.domain.LostItem;
import com.daedan.festabook.lostitem.domain.PickupStatus;
import com.daedan.festabook.organization.domain.Organization;

public record LostItemRequest(
        String imageUrl,
        String storageLocation
) {

    public LostItem toLostItem(Organization organization) {
        return new LostItem(
                organization,
                imageUrl,
                storageLocation,
                PickupStatus.PENDING
        );
    }
}
