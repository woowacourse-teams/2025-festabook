package com.daedan.festabook.place.domain;

import com.daedan.festabook.festival.domain.Coordinate;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;

public class PlaceFixture {

    private static final Festival DEFAULT_FESTIVAL = FestivalFixture.create();
    private static final PlaceCategory DEFAULT_CATEGORY = PlaceCategory.BAR;
    private static final Coordinate DEFAULT_COORDINATE = new Coordinate(37.3595704, 127.105399);

    public static Place create() {
        return new Place(
                DEFAULT_FESTIVAL,
                DEFAULT_CATEGORY,
                DEFAULT_COORDINATE
        );
    }

    public static Place create(
            Long placeId
    ) {
        return new Place(
                placeId,
                DEFAULT_FESTIVAL,
                DEFAULT_CATEGORY,
                DEFAULT_COORDINATE
        );
    }

    public static Place create(
            Festival festival
    ) {
        return new Place(
                festival,
                DEFAULT_CATEGORY,
                DEFAULT_COORDINATE
        );
    }

    public static Place create(
            Festival festival,
            PlaceCategory category,
            Double latitude,
            Double longitude
    ) {
        return new Place(
                festival,
                category,
                new Coordinate(latitude, longitude)
        );
    }

    public static Place create(
            Festival festival,
            PlaceCategory category,
            Coordinate coordinate
    ) {
        return new Place(
                festival,
                category,
                coordinate
        );
    }

    public static Place create(
            Long placeId,
            Festival festival,
            Double latitude,
            Double longitude
    ) {
        return new Place(
                placeId,
                festival,
                DEFAULT_CATEGORY,
                new Coordinate(latitude, longitude)
        );
    }

    public static Place create(
            Festival festival,
            PlaceCategory category
    ) {
        return new Place(
                festival,
                category,
                DEFAULT_COORDINATE
        );
    }

    public static Place createWithNullDefaults(
            Long placeId,
            Festival festival,
            PlaceCategory placeCategory
    ) {
        return new Place(
                placeId,
                festival,
                placeCategory,
                null
        );
    }
}
