package com.daedan.festabook.place.domain;

import com.daedan.festabook.device.domain.Device;

public class PlaceFavoriteFixture {

    public static PlaceFavorite create(
            Long id,
            Place place,
            Device device
    ) {
        return new PlaceFavorite(
                id,
                place,
                device
        );
    }

    public static PlaceFavorite create(
            Place place,
            Device device
    ) {
        return new PlaceFavorite(
                place,
                device
        );
    }
}
