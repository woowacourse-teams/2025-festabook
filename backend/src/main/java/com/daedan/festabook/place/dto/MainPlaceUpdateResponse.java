package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;

public record MainPlaceUpdateResponse(
        PlaceCategory placeCategory,
        String title,
        String description,
        String location,
        String host,
        @JsonFormat(pattern = "HH:mm") LocalTime startTime,
        @JsonFormat(pattern = "HH:mm") LocalTime endTime
) {

    public static MainPlaceUpdateResponse from(Place place) {
        return new MainPlaceUpdateResponse(
                place.getCategory(),
                place.getTitle(),
                place.getDescription(),
                place.getLocation(),
                place.getHost(),
                place.getStartTime(),
                place.getEndTime()
        );
    }
}
