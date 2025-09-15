package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.Coordinate;
import java.time.LocalDate;
import java.util.List;

public class FestivalCreateRequestFixture {

    public static FestivalCreateRequest create(
            String universityName,
            String festivalName,
            LocalDate startDate,
            LocalDate endDate,
            Integer zoom,
            Coordinate centerCoordinate,
            List<Coordinate> polygonHoleBoundary,
            String lostItemGuide
    ) {
        return new FestivalCreateRequest(
                universityName,
                festivalName,
                startDate,
                endDate,
                zoom,
                centerCoordinate,
                polygonHoleBoundary,
                lostItemGuide
        );
    }
}
