package com.daedan.festabook.festival.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record FestivalImageSequenceUpdateRequest(

        @Schema(description = "축제 이미지 ID", example = "1")
        Long festivalImageId,

        @Schema(description = "변경할 순서", example = "1")
        Integer sequence
) {
}
