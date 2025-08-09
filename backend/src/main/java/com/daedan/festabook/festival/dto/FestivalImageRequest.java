package com.daedan.festabook.festival.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record FestivalImageRequest(

        @Schema(description = "축제 이미지", example = "https://example.com/images/festival-image.jpg")
        String imageUrl
) {
}
