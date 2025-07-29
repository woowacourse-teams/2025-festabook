package com.daedan.festabook.place.dto;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceRequest(

        @Schema(description = "플레이스 카테고리", example = "BAR")
        PlaceCategory placeCategory
) {

    public Place toPlace(Organization organization) {
        return new Place(
                organization,
                placeCategory,
                null
        );
    }
}
