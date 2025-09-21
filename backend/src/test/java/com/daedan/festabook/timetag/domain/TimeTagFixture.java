package com.daedan.festabook.timetag.domain;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;

public class TimeTagFixture {

    private static final Festival DEFAULT_FESTIVAL = FestivalFixture.create();
    private static final String DEFAULT_NAME = "1일차 낮";

    public static TimeTag createWithFestival(Festival festival) {
        return new TimeTag(festival, DEFAULT_NAME);
    }

    public static TimeTag createWithName(String name) {
        return new TimeTag(DEFAULT_FESTIVAL, name);
    }
}
