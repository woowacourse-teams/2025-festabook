package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.Coordinate;
import com.daedan.festabook.festival.domain.CoordinateFixture;
import java.time.LocalDate;
import java.util.List;

public class FestivalCreateRequestFixture {

    private static final String DEFAULT_UNIVERSITY_NAME = "서울시립대학교";
    private static final String DEFAULT_FESTIVAL_NAME = "2025 시립 Water Festival: AQUA WAVE";
    private static final LocalDate DEFAULT_START_DATE = LocalDate.of(2025, 10, 15);
    private static final LocalDate DEFAULT_END_DATE = LocalDate.of(2025, 10, 17);
    private static final Integer DEFAULT_ZOOM = 16;
    private static final Coordinate DEFAULT_CENTER_COORDINATE = CoordinateFixture.create();
    private static final List<Coordinate> DEFAULT_POLYGON_HOLE_BOUNDARY = List.of(
            new Coordinate(37.5863397, 127.0571267),
            new Coordinate(37.5801841, 127.0562684),
            new Coordinate(37.5839591, 127.0638215)
    );
    private static final String DEFAULT_LOST_ITEM_GUIDE = "습득하신 분실물 혹은 수령 문의는 학생회 인스타그램 DM으로 전송해주세요.";

    public static FestivalCreateRequest create() {
        return new FestivalCreateRequest(
                DEFAULT_UNIVERSITY_NAME,
                DEFAULT_FESTIVAL_NAME,
                DEFAULT_START_DATE,
                DEFAULT_END_DATE,
                DEFAULT_ZOOM,
                DEFAULT_CENTER_COORDINATE,
                DEFAULT_POLYGON_HOLE_BOUNDARY,
                DEFAULT_LOST_ITEM_GUIDE
        );
    }

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
