package com.daedan.festabook.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceImageSequenceUpdateRequest(

        @Schema(description = "플레이스 이미지 ID", example = "1")
        Long placeImageId,

        @Schema(description = "변경할 순서", example = "1")
        Integer sequence
) {
}
