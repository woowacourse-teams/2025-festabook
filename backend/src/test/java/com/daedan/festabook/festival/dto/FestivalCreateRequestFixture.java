package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.Coordinate;
import java.time.LocalDate;
import java.util.List;

public class FestivalCreateRequestFixture {

    private static final String DEFAULT_LOST_ITEM_GUIDE = "습득하신 분실물 혹은 수령 문의는 학생회 인스타그램 DM으로 전송해주세요.";

    public static FestivalCreateRequest create(
            String universityName,
            String festivalName,
            LocalDate startDate,
            LocalDate endDate,
            Integer zoom,
            Coordinate centerCoordinate,
            List<Coordinate> polygonHoleBoundary
    ) {
        return new FestivalCreateRequest(
                universityName,
                festivalName,
                startDate,
                endDate,
                zoom,
                centerCoordinate,
                polygonHoleBoundary,
                DEFAULT_LOST_ITEM_GUIDE
        );
    }
}
