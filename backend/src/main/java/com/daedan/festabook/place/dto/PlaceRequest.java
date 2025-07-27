package com.daedan.festabook.place.dto;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceDetail;
import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceRequest(

        @Schema(description = "플레이스 카테고리", example = "SMOKING")
        PlaceCategory category,

        @Schema(description = "플레이스 이름", example = "웃지않는 미소 동아리")
        String title
) {

    public Place toPlace(Organization organization) {
        return new Place(
                organization,
                category
        );
    }

    public PlaceDetail toPlaceDetail(Organization organization) {
        return new PlaceDetail(
                new Place(organization, category),
                title
        );
    }
}
