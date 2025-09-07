package com.daedan.festabook.place.dto;

import com.daedan.festabook.festival.domain.Coordinate;
import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceCoordinateRequest(

        @Schema(description = "플레이스 좌표 위도", example = "37.5848056")
        Double latitude,

        @Schema(description = "플레이스 좌표 경도", example = "127.0600224")
        Double longitude
) {

    public Coordinate toCoordinate() {
        return new Coordinate(
                latitude,
                longitude
        );
    }
}
