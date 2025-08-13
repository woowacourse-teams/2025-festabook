package com.daedan.festabook.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceImageRequest(

        @Schema(description = "플레이스 이미지 url", example = "https://festabook.net/image/poster.jpg")
        String imageUrl
) {
}
