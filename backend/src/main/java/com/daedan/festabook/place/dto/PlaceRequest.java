package com.daedan.festabook.place.dto;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceRequest(

        @Schema(description = "플레이스 카테고리", example = "BAR")
        PlaceCategory placeCategory,

        @Schema(description = "플레이스 이름", example = "정문 주차장")
        String title
) {

    public Place toPlace(Festival festival) {
        return new Place(
                festival,
                placeCategory,
                title
        );
    }
}
