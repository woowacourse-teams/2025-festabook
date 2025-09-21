package com.daedan.festabook.timetag.domain;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.fixture.BaseEntityTestHelper;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TimeTagFixture {

    private static final Festival DEFAULT_FESTIVAL = FestivalFixture.create();
    private static final String DEFAULT_NAME = "1일차 낮";

    public static TimeTag createDefault() {
        return new TimeTag(DEFAULT_FESTIVAL, DEFAULT_NAME);
    }

    public static TimeTag createWithFestivalAndId(Festival festival, Long id) {
        TimeTag timeTag = new TimeTag(festival, DEFAULT_NAME);
        BaseEntityTestHelper.setId(timeTag, id);
        return timeTag;
    }

    public static TimeTag createWithFestival(Festival festival) {
        return new TimeTag(festival, DEFAULT_NAME);
    }

    public static TimeTag createWithName(String name) {
        return new TimeTag(DEFAULT_FESTIVAL, name);
    }

    public static List<TimeTag> createWithFestivalList(Festival festival, int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> createWithFestival(festival))
                .collect(Collectors.toList());
    }
}
