package com.daedan.festabook.place.dto;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;

public record EtcPlaceRequest(
        PlaceCategory placeCategory
) {

    public Place toPlace(Organization organization) {
        return new Place(
                organization,
                placeCategory
        );
    }
}
