package com.daedan.festabook.organization.domain;

import java.time.LocalDate;
import java.util.List;

public class OrganizationFixture {

    private static final String DEFAULT_UNIVERSITY_NAME = "서울시립대학교";
    private static final String DEFAULT_FESTIVAL_NAME = "2025 시립 Water Festival: AQUA WAVE";
    private static final LocalDate DEFAULT_START_DATE = LocalDate.of(2025, 10, 15);
    private static final LocalDate DEFAULT_END_DATE = LocalDate.of(2025, 10, 17);
    private static final Integer DEFAULT_ZOOM = 16;
    private static final Coordinate DEFAULT_CENTER_COORDINATE = new Coordinate(37.3595704, 127.105399);
    private static final List<Coordinate> DEFAULT_POLYGON_HOLE_BOUNDARY = List.of(
            new Coordinate(37.5863397, 127.0571267),
            new Coordinate(37.5801841, 127.0562684),
            new Coordinate(37.5839591, 127.0638215)
    );

    public static Organization create() {
        return new Organization(
                DEFAULT_UNIVERSITY_NAME,
                DEFAULT_FESTIVAL_NAME,
                DEFAULT_START_DATE,
                DEFAULT_END_DATE,
                DEFAULT_ZOOM,
                DEFAULT_CENTER_COORDINATE,
                DEFAULT_POLYGON_HOLE_BOUNDARY
        );
    }

    public static Organization create(
            String universityName
    ) {
        return new Organization(
                universityName,
                DEFAULT_FESTIVAL_NAME,
                DEFAULT_START_DATE,
                DEFAULT_END_DATE,
                DEFAULT_ZOOM,
                DEFAULT_CENTER_COORDINATE,
                DEFAULT_POLYGON_HOLE_BOUNDARY
        );
    }

    public static Organization create(
            Integer zoom
    ) {
        return new Organization(
                DEFAULT_UNIVERSITY_NAME,
                DEFAULT_FESTIVAL_NAME,
                DEFAULT_START_DATE,
                DEFAULT_END_DATE,
                zoom,
                DEFAULT_CENTER_COORDINATE,
                DEFAULT_POLYGON_HOLE_BOUNDARY
        );
    }

    public static Organization create(
            Coordinate centerCoordinate
    ) {
        return new Organization(
                DEFAULT_UNIVERSITY_NAME,
                DEFAULT_FESTIVAL_NAME,
                DEFAULT_START_DATE,
                DEFAULT_END_DATE,
                DEFAULT_ZOOM,
                centerCoordinate,
                DEFAULT_POLYGON_HOLE_BOUNDARY
        );
    }

    public static Organization create(
            List<Coordinate> polygonHoleBoundary
    ) {
        return new Organization(
                DEFAULT_UNIVERSITY_NAME,
                DEFAULT_FESTIVAL_NAME,
                DEFAULT_START_DATE,
                DEFAULT_END_DATE,
                DEFAULT_ZOOM,
                DEFAULT_CENTER_COORDINATE,
                polygonHoleBoundary
        );
    }

    public static Organization create(
            Long id
    ) {
        return new Organization(
                id,
                DEFAULT_UNIVERSITY_NAME,
                DEFAULT_FESTIVAL_NAME,
                DEFAULT_START_DATE,
                DEFAULT_END_DATE,
                DEFAULT_ZOOM,
                DEFAULT_CENTER_COORDINATE,
                DEFAULT_POLYGON_HOLE_BOUNDARY
        );
    }
}
