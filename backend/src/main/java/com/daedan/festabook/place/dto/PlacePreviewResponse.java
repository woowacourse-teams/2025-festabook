package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.timetag.domain.TimeTag;
import com.daedan.festabook.timetag.dto.TimeTagResponses;
import java.util.List;

public record PlacePreviewResponse(
        Long placeId,
        String imageUrl,
        PlaceCategory category,
        String title,
        String description,
        String location,
        TimeTagResponses timeTags
) {

    public static PlacePreviewResponse from(Place place, PlaceImage placeImage, List<TimeTag> timeTags) {
        return new PlacePreviewResponse(
                place.getId(),
                placeImage != null ? placeImage.getImageUrl() : null,
                place.getCategory(),
                place.getTitle(),
                place.getDescription(),
                place.getLocation(),
                TimeTagResponses.from(timeTags)
        );
    }
}
