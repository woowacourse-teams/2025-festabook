package com.daedan.festabook.place.domain;

import com.daedan.festabook.festival.domain.Coordinate;
import com.daedan.festabook.festival.domain.CoordinateFixture;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.fixture.BaseEntityTestHelper;
import java.time.LocalTime;

public class PlaceFixture {

    private static final Festival DEFAULT_FESTIVAL = FestivalFixture.create();
    private static final PlaceCategory DEFAULT_CATEGORY = PlaceCategory.BAR;
    private static final Coordinate DEFAULT_COORDINATE = CoordinateFixture.create();
    private static final String DEFAULT_TITLE = "코딩하며 한잔";
    private static final String DEFAULT_DESCRIPTION = "시원한 맥주와 맛있는 치킨!";
    private static final String DEFAULT_LOCATION = "공학관 앞";
    private static final String DEFAULT_HOST = "C블C블";
    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(9, 0);
    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(18, 0);

    public static Place create() {
        return new Place(
                DEFAULT_FESTIVAL,
                DEFAULT_CATEGORY,
                DEFAULT_COORDINATE,
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
    }

    public static Place create(
            Long placeId
    ) {
        Place place = new Place(
                DEFAULT_FESTIVAL,
                DEFAULT_CATEGORY,
                DEFAULT_COORDINATE,
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
        BaseEntityTestHelper.setId(place, placeId);
        return place;
    }

    public static Place create(
            Festival festival
    ) {
        return new Place(
                festival,
                DEFAULT_CATEGORY,
                DEFAULT_COORDINATE,
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
    }

    public static Place create(
            PlaceCategory placeCategory
    ) {
        return new Place(
                DEFAULT_FESTIVAL,
                placeCategory,
                DEFAULT_COORDINATE,
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
    }

    public static Place create(
            Festival festival,
            Long placeId
    ) {
        Place place = new Place(
                festival,
                DEFAULT_CATEGORY,
                DEFAULT_COORDINATE,
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
        BaseEntityTestHelper.setId(place, placeId);
        return place;
    }

    public static Place create(
            Festival festival,
            PlaceCategory category,
            Coordinate coordinate
    ) {
        return new Place(
                festival,
                category,
                coordinate,
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
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
                CoordinateFixture.create(latitude, longitude),
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
    }

    public static Place create(
            Festival festival,
            Double latitude,
            Double longitude,
            Long placeId
    ) {
        Place place = new Place(
                festival,
                DEFAULT_CATEGORY,
                CoordinateFixture.create(latitude, longitude),
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
        BaseEntityTestHelper.setId(place, placeId);
        return place;
    }

    public static Place create(
            Festival festival,
            PlaceCategory placeCategory,
            Coordinate coordinate,
            String title,
            String description,
            String location,
            String host,
            LocalTime startTime,
            LocalTime endTime
    ) {
        return new Place(
                festival,
                placeCategory,
                coordinate,
                title,
                description,
                location,
                host,
                startTime,
                endTime
        );
    }

    public static Place createWithTitle(
            String title
    ) {
        return new Place(
                DEFAULT_FESTIVAL,
                DEFAULT_CATEGORY,
                DEFAULT_COORDINATE,
                title,
                DEFAULT_DESCRIPTION,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
    }

    public static Place createWithDescription(
            String description
    ) {
        return new Place(
                DEFAULT_FESTIVAL,
                DEFAULT_CATEGORY,
                DEFAULT_COORDINATE,
                DEFAULT_TITLE,
                description,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
    }

    public static Place createWithLocation(
            String location
    ) {
        return new Place(
                DEFAULT_FESTIVAL,
                DEFAULT_CATEGORY,
                DEFAULT_COORDINATE,
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                location,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
    }

    public static Place createWithHost(
            String host
    ) {
        return new Place(
                DEFAULT_FESTIVAL,
                DEFAULT_CATEGORY,
                DEFAULT_COORDINATE,
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                DEFAULT_LOCATION,
                host,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
    }

    public static Place createWithTime(
            LocalTime startTime,
            LocalTime endTime
    ) {
        return new Place(
                DEFAULT_FESTIVAL,
                DEFAULT_CATEGORY,
                DEFAULT_COORDINATE,
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                startTime,
                endTime
        );
    }

    public static Place createWithNullDefaults(
            Festival festival,
            PlaceCategory placeCategory,
            String title,
            Long placeId
    ) {
        Place place = new Place(
                festival,
                placeCategory,
                null,
                title,
                null,
                null,
                null,
                null,
                null
        );
        BaseEntityTestHelper.setId(place, placeId);
        return place;
    }
}
