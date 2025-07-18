package com.daedan.festabook.place.domain;

import com.daedan.festabook.device.domain.Device;

public class PlaceBookmarkFixture {

    public static PlaceBookmark create(
            Place place,
            Device device
    ) {
        return new PlaceBookmark(
                place,
                device
        );
    }
}
