package com.daedan.festabook.organization.dto;

import com.daedan.festabook.organization.domain.FestivalImage;

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
