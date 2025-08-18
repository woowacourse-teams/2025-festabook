package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceImage;

public record PlaceMainPreviewResponse(
        Long placeId,
        String imageUrl,
        PlaceCategory category,
        String title,
        String description,
        String location
) {

    public static PlaceMainPreviewResponse from(Place place, PlaceImage placeImage) {
        return new PlaceMainPreviewResponse(
                place.getId(),
                placeImage != null ? placeImage.getImageUrl() : null,
                place.getCategory(),
                place.getTitle(),
                place.getDescription(),
                place.getLocation()
        );
    }
}
