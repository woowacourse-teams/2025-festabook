package com.daedan.festabook.place.domain;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.global.fixture.BaseEntityTestHelper;

public class PlaceFavoriteFixture {

    public static PlaceFavorite create(
            Place place,
            Device device
    ) {
        return new PlaceFavorite(
                place,
                device
        );
    }

    public static PlaceFavorite create(
            Place place,
            Device device,
            Long placeFavoriteId
    ) {
        PlaceFavorite placeFavorite = new PlaceFavorite(place, device);
        return BaseEntityTestHelper.setId(placeFavorite, placeFavoriteId);
    }
}
