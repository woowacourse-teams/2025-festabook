package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceCategory;
import java.time.LocalTime;

public record PlaceUpdateRequest(
        PlaceCategory placeCategory,
        String title,
        String description,
        String location,
        String host,
        LocalTime startTime,
        LocalTime endTime
) {
}
