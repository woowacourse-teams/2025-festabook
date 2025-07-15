package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceImage;

public record PlaceListResponse(
        Long id,
        String imageUrl,
        PlaceCategory category,
        String title,
        String description,
        String location
) {

    public static PlaceListResponse from(Place place, PlaceImage placeImage) {
        return new PlaceListResponse(
                place.getId(),
                placeImage.getImageUrl(),
                place.getCategory(),
                place.getTitle(),
                place.getDescription(),
                place.getLocation()
        );
    }
}
