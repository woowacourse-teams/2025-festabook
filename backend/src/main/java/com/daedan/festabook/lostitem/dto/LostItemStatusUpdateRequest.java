package com.daedan.festabook.lostitem.dto;

import com.daedan.festabook.lostitem.domain.PickupStatus;

public record LostItemStatusUpdateRequest(
        PickupStatus status
) {
}
