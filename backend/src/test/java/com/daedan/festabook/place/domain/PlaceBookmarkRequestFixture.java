package com.daedan.festabook.place.domain;

import com.daedan.festabook.place.dto.PlaceBookmarkRequest;

public class PlaceBookmarkRequestFixture {


    public static PlaceBookmarkRequest create(
            Long deviceId
    ) {
        return new PlaceBookmarkRequest(
                deviceId
        );
    }
}
