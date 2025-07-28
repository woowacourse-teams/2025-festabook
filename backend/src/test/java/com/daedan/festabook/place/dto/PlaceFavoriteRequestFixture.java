package com.daedan.festabook.place.dto;

public class PlaceFavoriteRequestFixture {

    public static PlaceFavoriteRequest create(
            Long deviceId
    ) {
        return new PlaceFavoriteRequest(
                deviceId
        );
    }
}
