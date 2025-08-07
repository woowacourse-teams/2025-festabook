package com.daedan.festabook.lostitem.dto;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.lostitem.domain.LostItem;
import com.daedan.festabook.lostitem.domain.PickupStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record LostItemRequest(

        @Schema(description = "분실물 보관 위치", example = "총학생회 부스")
        String storageLocation,

        @Schema(description = "분실물 이미지 URL", example = "https://example.com/images/lost-item.jpg")
        String imageUrl
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
