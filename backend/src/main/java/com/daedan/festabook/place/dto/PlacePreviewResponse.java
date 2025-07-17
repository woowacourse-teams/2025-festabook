package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceImage;

public record PlacePreviewResponse(
        Long id,
        String imageUrl,
        PlaceCategory category,
        String title,
        String description,
        String location
) {

    public static PlacePreviewResponse from(Place place, PlaceImage placeImage) {
        return new PlacePreviewResponse(
                place.getId(),
                placeImage != null ? placeImage.getImageUrl() : null,
                place.getCategory(),
                place.getTitle(),
                place.getDescription(),
                place.getLocation()
        );
    }
}
