package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceDetail;
import com.daedan.festabook.place.domain.PlaceImage;

public record PlacePreviewResponse(
        Long id,
        String imageUrl,
        PlaceCategory category,
        String title,
        String description,
        String location
) {

    public static PlacePreviewResponse from(Place place, PlaceDetail placeDetail, PlaceImage placeImage) {
        if (placeDetail == null) {
            return new PlacePreviewResponse(
                    place.getId(),
                    null,
                    place.getCategory(),
                    null,
                    null,
                    null
            );
        }
        return new PlacePreviewResponse(
                place.getId(),
                placeImage != null ? placeImage.getImageUrl() : null,
                place.getCategory(),
                placeDetail.getTitle(),
                placeDetail.getDescription(),
                placeDetail.getLocation()
        );
    }
}
