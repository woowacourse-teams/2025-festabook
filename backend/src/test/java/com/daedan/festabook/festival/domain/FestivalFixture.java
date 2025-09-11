package com.daedan.festabook.festival.domain;

import com.daedan.festabook.global.fixture.BaseEntityTestHelper;
import java.time.LocalDate;
import java.util.List;

public class FestivalFixture {

    private static final String DEFAULT_UNIVERSITY_NAME = "서울시립대학교";
    private static final String DEFAULT_FESTIVAL_NAME = "2025 시립 Water Festival: AQUA WAVE";
    private static final LocalDate DEFAULT_START_DATE = LocalDate.of(2025, 10, 15);
    private static final LocalDate DEFAULT_END_DATE = LocalDate.of(2025, 10, 17);
    private static final boolean DEFAULT_USER_VISIBLE = true;
    private static final Integer DEFAULT_ZOOM = 16;
    private static final Coordinate DEFAULT_CENTER_COORDINATE = CoordinateFixture.create();
    private static final List<Coordinate> DEFAULT_POLYGON_HOLE_BOUNDARY = List.of(
            new Coordinate(37.5863397, 127.0571267),
            new Coordinate(37.5801841, 127.0562684),
            new Coordinate(37.5839591, 127.0638215)
    );

    public static Festival create() {
        return new Festival(
                DEFAULT_UNIVERSITY_NAME,
                DEFAULT_FESTIVAL_NAME,
                DEFAULT_START_DATE,
                DEFAULT_END_DATE,
                DEFAULT_USER_VISIBLE,
                DEFAULT_ZOOM,
                DEFAULT_CENTER_COORDINATE,
                DEFAULT_POLYGON_HOLE_BOUNDARY
        );
    }

    public static Festival create(
            String universityName
    ) {
        return new Festival(
                universityName,
                DEFAULT_FESTIVAL_NAME,
                DEFAULT_START_DATE,
                DEFAULT_END_DATE,
                DEFAULT_USER_VISIBLE,
                DEFAULT_ZOOM,
                DEFAULT_CENTER_COORDINATE,
                DEFAULT_POLYGON_HOLE_BOUNDARY
        );
    }

    public static Festival create(
            Integer zoom
    ) {
        return new Festival(
                DEFAULT_UNIVERSITY_NAME,
                DEFAULT_FESTIVAL_NAME,
                DEFAULT_START_DATE,
                DEFAULT_END_DATE,
                DEFAULT_USER_VISIBLE,
                zoom,
                DEFAULT_CENTER_COORDINATE,
                DEFAULT_POLYGON_HOLE_BOUNDARY
        );
    }

    public static Festival create(
            String universityName,
            boolean userVisible
    ) {
        return new Festival(
                universityName,
                DEFAULT_FESTIVAL_NAME,
                DEFAULT_START_DATE,
                DEFAULT_END_DATE,
                userVisible,
                DEFAULT_ZOOM,
                DEFAULT_CENTER_COORDINATE,
                DEFAULT_POLYGON_HOLE_BOUNDARY
        );
    }

    public static Festival create(
            Coordinate centerCoordinate
    ) {
        return new Festival(
                DEFAULT_UNIVERSITY_NAME,
                DEFAULT_FESTIVAL_NAME,
                DEFAULT_START_DATE,
                DEFAULT_END_DATE,
                DEFAULT_USER_VISIBLE,
                DEFAULT_ZOOM,
                centerCoordinate,
                DEFAULT_POLYGON_HOLE_BOUNDARY
        );
    }

    public static Festival create(
            List<Coordinate> polygonHoleBoundary
    ) {
        return new Festival(
                DEFAULT_UNIVERSITY_NAME,
                DEFAULT_FESTIVAL_NAME,
                DEFAULT_START_DATE,
                DEFAULT_END_DATE,
                DEFAULT_USER_VISIBLE,
                DEFAULT_ZOOM,
                DEFAULT_CENTER_COORDINATE,
                polygonHoleBoundary
        );
    }

    public static Festival create(
            Long festivalId
    ) {
        Festival festival = new Festival(
                DEFAULT_UNIVERSITY_NAME,
                DEFAULT_FESTIVAL_NAME,
                DEFAULT_START_DATE,
                DEFAULT_END_DATE,
                DEFAULT_USER_VISIBLE,
                DEFAULT_ZOOM,
                DEFAULT_CENTER_COORDINATE,
                DEFAULT_POLYGON_HOLE_BOUNDARY
        );
        BaseEntityTestHelper.setId(festival, festivalId);
        return festival;
    }

    public static Festival create(
            LocalDate startDate,
            LocalDate endDate
    ) {
        return new Festival(
                DEFAULT_UNIVERSITY_NAME,
                DEFAULT_FESTIVAL_NAME,
                startDate,
                endDate,
                DEFAULT_USER_VISIBLE,
                DEFAULT_ZOOM,
                DEFAULT_CENTER_COORDINATE,
                DEFAULT_POLYGON_HOLE_BOUNDARY
        );
    }

    public static Festival create(
            String universityName,
            String festivalName,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return new Festival(
                universityName,
                festivalName,
                startDate,
                endDate,
                DEFAULT_ZOOM,
                DEFAULT_CENTER_COORDINATE,
                DEFAULT_POLYGON_HOLE_BOUNDARY
        );
    }
}
