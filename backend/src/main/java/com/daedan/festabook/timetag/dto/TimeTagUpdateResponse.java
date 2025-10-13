package com.daedan.festabook.timetag.dto;

import com.daedan.festabook.timetag.domain.TimeTag;

public record TimeTagUpdateResponse(
        Long id,
        Long festivalId,
        String name
) {

    public static TimeTagUpdateResponse from(TimeTag timeTag) {
        return new TimeTagUpdateResponse(
                timeTag.getId(),
                timeTag.getFestival().getId(),
                timeTag.getName()
        );
    }
}
