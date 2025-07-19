package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceBookmark;

public record PlaceBookmarkResponse(
        Long id
) {

    public static PlaceBookmarkResponse from(PlaceBookmark placeBookmark) {
        return new PlaceBookmarkResponse(
                placeBookmark.getId()
        );
    }
}
