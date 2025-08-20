package com.daedan.festabook.place.domain;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.global.fixture.BaseEntityTestHelper;

public class PlaceFavoriteFixture {

    public static PlaceFavorite create(
            Long placeFavoriteId,
            Place place,
            Device device
    ) {
        PlaceFavorite placeFavorite = new PlaceFavorite(place, device);
        return BaseEntityTestHelper.setId(placeFavorite, placeFavoriteId);
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
