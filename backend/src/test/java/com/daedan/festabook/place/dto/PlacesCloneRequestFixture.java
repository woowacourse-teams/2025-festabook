package com.daedan.festabook.place.dto;

import java.util.ArrayList;
import java.util.List;

public class PlacesCloneRequestFixture {

    private static final List<Long> DEFAULT_ORIGINAL_PLACE_ID = List.of(1L, 2L, 3L);

    public static PlacesCloneRequest create() {
        return new PlacesCloneRequest(
                new ArrayList<>(DEFAULT_ORIGINAL_PLACE_ID)
        );
    }

    public static PlacesCloneRequest create(
            List<Long> originalPlaceIds
    ) {
        return new PlacesCloneRequest(
                originalPlaceIds
        );
    }
}
