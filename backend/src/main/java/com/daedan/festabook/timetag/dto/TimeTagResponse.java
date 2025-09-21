package com.daedan.festabook.timetag.dto;

import com.daedan.festabook.timetag.domain.TimeTag;

public record TimeTagResponse(
        Long id,
        String name
) {

    public static TimeTagResponse from(TimeTag timeTag) {
        return new TimeTagResponse(
                timeTag.getId(),
                timeTag.getName()
        );
    }
}
