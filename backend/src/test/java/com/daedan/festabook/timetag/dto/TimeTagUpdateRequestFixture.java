package com.daedan.festabook.timetag.dto;

public class TimeTagUpdateRequestFixture {

    private static final String DEFAULT_NAME = "2일차 저녁";

    public static TimeTagUpdateRequest createDefault() {
        return new TimeTagUpdateRequest(
                DEFAULT_NAME
        );
    }
}
