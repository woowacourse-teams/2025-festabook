package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.FestivalImage;

public record FestivalImageResponse(
        Long festivalImageId,
        String imageUrl,
        Integer sequence
) {

    public static FestivalImageResponse from(FestivalImage festivalImage) {
        return new FestivalImageResponse(
                festivalImage.getId(),
                festivalImage.getImageUrl(),
                festivalImage.getSequence()
        );
    }
}
