package com.daedan.festabook.lostitem.controller;

import com.daedan.festabook.lostitem.domain.PickupStatus;

public record LostItemStatusRequest(
        PickupStatus status
) {
}
