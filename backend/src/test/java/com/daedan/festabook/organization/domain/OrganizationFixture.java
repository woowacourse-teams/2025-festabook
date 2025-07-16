package com.daedan.festabook.organization.domain;

import java.util.List;

public class OrganizationFixture {

    private static final String DEFAULT_NAME = "페스타북";
    private static final Integer DEFAULT_ZOOM = 16;
    private static final Coordinate DEFAULT_CENTER_COORDINATE = new Coordinate(37.3595704, 127.105399);
    private static final List<Coordinate> DEFAULT_POLYGON_HOLE_BOUNDARY = List.of(
            new Coordinate(37.5863397, 127.0571267),
            new Coordinate(37.5801841, 127.0562684),
            new Coordinate(37.5839591, 127.0638215)
    );

    public static Organization create() {
        return new Organization(
                DEFAULT_NAME,
                DEFAULT_ZOOM,
                DEFAULT_CENTER_COORDINATE,
                DEFAULT_POLYGON_HOLE_BOUNDARY
        );
    }

    public static Organization create(
            String name
    ) {
        return new Organization(
                name,
                DEFAULT_ZOOM,
                DEFAULT_CENTER_COORDINATE,
                DEFAULT_POLYGON_HOLE_BOUNDARY
        );
    }

    public static Organization create(
            Long id
    ) {
        return new Organization(
                id,
                DEFAULT_NAME,
                DEFAULT_ZOOM,
                DEFAULT_CENTER_COORDINATE,
                DEFAULT_POLYGON_HOLE_BOUNDARY
        );
    }
}
