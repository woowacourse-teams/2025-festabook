package com.daedan.festabook.place.domain;

import com.daedan.festabook.organization.domain.Coordinate;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;

public class PlaceFixture {

    private static final Organization DEFAULT_ORGANIZATION = OrganizationFixture.create();
    private static final PlaceCategory DEFAULT_CATEGORY = PlaceCategory.BAR;
    private static final Coordinate DEFAULT_COORDINATE = new Coordinate(37.3595704, 127.105399);

    public static Place create() {
        return new Place(
                DEFAULT_ORGANIZATION,
                DEFAULT_CATEGORY,
                DEFAULT_COORDINATE
        );
    }

    public static Place create(
            Long id
    ) {
        return new Place(
                id,
                DEFAULT_ORGANIZATION,
                DEFAULT_CATEGORY,
                DEFAULT_COORDINATE
        );
    }

    public static Place create(
            Organization organization
    ) {
        return new Place(
                organization,
                DEFAULT_CATEGORY,
                DEFAULT_COORDINATE
        );
    }

    public static Place create(
            Organization organization,
            PlaceCategory category,
            Double latitude,
            Double longitude
    ) {
        return new Place(
                organization,
                category,
                new Coordinate(latitude, longitude)
        );
    }

    public static Place create(
            Organization organization,
            PlaceCategory category
    ) {
        return new Place(
                organization,
                category,
                DEFAULT_COORDINATE
        );
    }

    public static Place createEmpty(
            Long id,
            Organization organization,
            PlaceCategory placeCategory
    ) {
        return new Place(
                id,
                organization,
                placeCategory,
                null
        );
    }
}
