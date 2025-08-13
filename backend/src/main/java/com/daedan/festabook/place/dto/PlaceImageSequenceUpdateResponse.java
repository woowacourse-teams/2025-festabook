package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceImage;

public record PlaceImageSequenceUpdateResponse(
        Long placeImageId,
        int sequence
) {

    public static PlaceImageSequenceUpdateResponse from(PlaceImage placeImage) {
        return new PlaceImageSequenceUpdateResponse(
                placeImage.getId(),
                placeImage.getSequence()
        );
    }
}
