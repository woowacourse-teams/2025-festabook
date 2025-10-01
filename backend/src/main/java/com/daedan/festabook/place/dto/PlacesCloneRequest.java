package com.daedan.festabook.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record PlacesCloneRequest(

        @Schema(description = "복제하려는 원본 플레이스 id들", example = "[1, 2, 3]")
        List<Long> originalPlaceIds
) {
}
