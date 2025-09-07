package com.daedan.festabook.lostitem.dto;

import com.daedan.festabook.lostitem.domain.PickupStatus;

public class LostItemStatusUpdateRequestFixture {

    private static final PickupStatus DEFAULT_STATUS = PickupStatus.PENDING;

    public static LostItemStatusUpdateRequest create() {
        return new LostItemStatusUpdateRequest(
                DEFAULT_STATUS
        );
    }

    public static LostItemStatusUpdateRequest create(
            PickupStatus status
    ) {
        return new LostItemStatusUpdateRequest(
                status
        );
    }
}
