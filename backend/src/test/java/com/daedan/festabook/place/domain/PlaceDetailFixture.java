package com.daedan.festabook.place.domain;

import java.time.LocalTime;

public class PlaceDetailFixture {

    private static final Place DEFAULT_PLACE = PlaceFixture.create();
    private static final String DEFAULT_TITLE = "코딩하며 한잔";
    private static final String DEFAULT_DESCRIPTION = "시원한 맥주와 맛있는 치킨!";
    private static final String DEFAULT_LOCATION = "공학관 앞";
    private static final String DEFAULT_HOST = "C블C블";
    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(9, 0);
    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(18, 0);

    public static PlaceDetail create(Place place) {
        return new PlaceDetail(
                place,
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
    }

    public static PlaceDetail create(
            String title,
            String description,
            String location,
            String host,
            LocalTime startTime,
            LocalTime endTime
    ) {
        return new PlaceDetail(
                DEFAULT_PLACE,
                title,
                description,
                location,
                host,
                startTime,
                endTime
        );
    }

    public static PlaceDetail createWithTitle(String title) {
        return new PlaceDetail(
                DEFAULT_PLACE,
                title,
                DEFAULT_DESCRIPTION,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
    }

    public static PlaceDetail createWithDescription(String description) {
        return new PlaceDetail(
                DEFAULT_PLACE,
                DEFAULT_TITLE,
                description,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
    }

    public static PlaceDetail createWithLocation(String location) {
        return new PlaceDetail(
                DEFAULT_PLACE,
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                location,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
    }

    public static PlaceDetail createWithHost(String host) {
        return new PlaceDetail(
                DEFAULT_PLACE,
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                DEFAULT_LOCATION,
                host,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
    }

    public static PlaceDetail createWithTime(LocalTime startTime, LocalTime endTime) {
        return new PlaceDetail(
                DEFAULT_PLACE,
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                startTime,
                endTime
        );
    }
}
