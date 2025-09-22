package com.daedan.festabook.timetag.dto;

import com.daedan.festabook.timetag.domain.TimeTag;

public record TimeTagCreateResponse(
        Long timeTagId,
        Long festivalId,
        String name
) {

    public static TimeTagCreateResponse from(TimeTag timeTag) {
        return new TimeTagCreateResponse(
                timeTag.getId(),
                timeTag.getFestival().getId(),
                timeTag.getName()
        );
    }
}
