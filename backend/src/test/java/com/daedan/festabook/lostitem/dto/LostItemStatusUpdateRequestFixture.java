package com.daedan.festabook.lostitem.dto;

import com.daedan.festabook.lostitem.controller.LostItemStatusRequest;
import com.daedan.festabook.lostitem.domain.PickupStatus;

public class LostItemStatusUpdateRequestFixture {

    private static final PickupStatus DEFAULT_STATUS = PickupStatus.PENDING;

    public static LostItemStatusRequest create() {
        return create(
                DEFAULT_STATUS
        );
    }

    public static LostItemStatusRequest create(
            PickupStatus status
    ) {
        return new LostItemStatusRequest(
                status
        );
    }
}
