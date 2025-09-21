package com.daedan.festabook.timetag.dto;

public class TimeTagCreateRequestFixture {

    private static final String DEFAULT_NAME = "1일차 낮";

    public static TimeTagCreateRequest createDefault() {
        return new TimeTagCreateRequest(
                DEFAULT_NAME
        );
    }
}
