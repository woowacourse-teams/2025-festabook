package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;

public record PlaceResponse(
        Long id,
        String title,
        String content,
        PlaceCategory category,
        String location,
        String host,
        @JsonFormat(pattern = "HH:mm") LocalTime startTime,
        @JsonFormat(pattern = "HH:mm") LocalTime endTime
) {

    public static PlaceResponse from(Place place) {
        return new PlaceResponse(
                place.getId(),
                place.getTitle(),
                place.getContent(),
                place.getCategory(),
                place.getLocation(),
                place.getHost(),
                place.getStartTime(),
                place.getEndTime()
        );
    }
}
