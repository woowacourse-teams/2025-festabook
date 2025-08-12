package com.daedan.festabook.lostitem.dto;

import com.daedan.festabook.lostitem.domain.PickupStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record LostItemStatusUpdateRequest(

        @Schema(description = "분실물 상태", example = "PENDING")
        PickupStatus pickupStatus
) {
}
