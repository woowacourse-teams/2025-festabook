package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import java.util.List;

public record EtcPlaceUpdateResponse(
        String title,
        List<Long> timeTags
) {

    public static EtcPlaceUpdateResponse from(Place place, List<Long> timeTags) {
        return new EtcPlaceUpdateResponse(
                place.getTitle(),
                timeTags
        );
    }
}
