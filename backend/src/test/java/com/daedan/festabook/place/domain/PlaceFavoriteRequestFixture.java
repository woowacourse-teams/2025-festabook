package com.daedan.festabook.place.domain;

import com.daedan.festabook.place.dto.PlaceFavoriteRequest;

public class PlaceFavoriteRequestFixture {

    public static PlaceFavoriteRequest create(
            Long deviceId
    ) {
        return new PlaceFavoriteRequest(
                deviceId
        );
    }
}
