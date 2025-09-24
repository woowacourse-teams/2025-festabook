package com.daedan.festabook.place.dto;

import java.util.ArrayList;
import java.util.List;

public class PlaceBulkCloneRequestFixture {

    private static final List<Long> DEFAULT_ORIGINAL_PLACE_ID = List.of(1L, 2L, 3L);

    public static PlaceBulkCloneRequest create() {
        return new PlaceBulkCloneRequest(
                new ArrayList<>(DEFAULT_ORIGINAL_PLACE_ID)
        );
    }

    public static PlaceBulkCloneRequest create(
            List<Long> originalPlaceIds
    ) {
        return new PlaceBulkCloneRequest(
                originalPlaceIds
        );
    }
}
