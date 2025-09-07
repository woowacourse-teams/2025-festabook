package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.Coordinate;
import com.daedan.festabook.festival.domain.Festival;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public record FestivalCreateRequest(

        @Schema(description = "학교 이름", example = "서울시립대학교")
        String universityName,

        @Schema(description = "축제 이름", example = "2025 시립 Water Festival\n: AQUA WAVE")
        String festivalName,

        @Schema(description = "시작일", example = "2025-08-23")
        LocalDate startDate,

        @Schema(description = "종료일", example = "2025-08-25")
        LocalDate endDate,

        @Schema(description = "지도 줌 배율", example = "7")
        Integer zoom,

        @Schema(description = "지도 중심 좌표", example = "{\"latitude\": 37.5862037, \"longitude\": 127.0565152}")
        Coordinate centerCoordinate,

        @Schema(
                description = "지도 다각형 좌표",
                example = "[{\"latitude\": 37.5862037, \"longitude\": 127.0565152}, " +
                        "{\"latitude\": 37.5845543, \"longitude\": 127.0555925}, " +
                        "{\"latitude\": 37.5828198, \"longitude\": 127.0550775}, " +
                        "{\"latitude\": 37.5818676, \"longitude\": 127.0565795}, " +
                        "{\"latitude\": 37.5823607, \"longitude\": 127.0627164}, " +
                        "{\"latitude\": 37.5848773, \"longitude\": 127.063961}, " +
                        "{\"latitude\": 37.5854215, \"longitude\": 127.0628237}, " +
                        "{\"latitude\": 37.5850304, \"longitude\": 127.0599269}, " +
                        "{\"latitude\": 37.5862547, \"longitude\": 127.0591545}, " +
                        "{\"latitude\": 37.5865607, \"longitude\": 127.0569872}]"
        )
        List<Coordinate> polygonHoleBoundary
) {

    public Festival toEntity() {
        return new Festival(
                universityName,
                festivalName,
                startDate,
                endDate,
                zoom,
                centerCoordinate,
                polygonHoleBoundary
        );
    }
}
