package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.Coordinate;
import com.daedan.festabook.festival.domain.Festival;
import java.time.LocalDate;
import java.util.List;

public record FestivalCreateResponse(
        Long festivalId,
        String universityName,
        String festivalName,
        LocalDate startDate,
        LocalDate endDate,
        boolean userVisible,
        Integer zoom,
        Coordinate centerCoordinate,
        List<Coordinate> polygonHoleBoundary
) {

    public static FestivalCreateResponse from(Festival festival) {
        return new FestivalCreateResponse(
                festival.getId(),
                festival.getUniversityName(),
                festival.getFestivalName(),
                festival.getStartDate(),
                festival.getEndDate(),
                festival.isUserVisible(),
                festival.getZoom(),
                festival.getCenterCoordinate(),
                festival.getPolygonHoleBoundary()
        );
    }
}
