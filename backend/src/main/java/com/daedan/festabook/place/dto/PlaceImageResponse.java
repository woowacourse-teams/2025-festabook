package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceImage;

public record PlaceImageResponse(
        Long id,
        String imageUrl
) {

    public static PlaceImageResponse from(PlaceImage placeImage) {
        return new PlaceImageResponse(
                placeImage.getId(),
                placeImage.getImageUrl()
        );
    }
}
