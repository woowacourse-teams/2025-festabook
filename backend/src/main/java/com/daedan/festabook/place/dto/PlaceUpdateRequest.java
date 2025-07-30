package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

public record PlaceUpdateRequest(

        @Schema(description = "플레이스 카테고리", example = "BAR")
        PlaceCategory placeCategory,

        @Schema(description = "플레이스 이름", example = "미소 된장술밥 주점")
        String title,

        @Schema(description = "플레이스 설명", example = "미소된장으로 만든 술밥을 판매중입니다.")
        String description,

        @Schema(description = "플레이스 간단한 위치 정보", example = "공대식당 맞은편")
        String location,

        @Schema(description = "플레이스 호스트", example = "미소")
        String host,

        @Schema(description = "플레이스 문여는 시간", example = "13:00")
        LocalTime startTime,

        @Schema(description = "플레이스 문닫는 시간", example = "23:00")
        LocalTime endTime
) {

    public PlaceDetail toPlaceDetail(Place place) {
        return new PlaceDetail(
                place,
                title,
                description,
                location,
                host,
                startTime,
                endTime
        );
    }
}

