package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceFavorite;

public record PlaceFavoriteResponse(
        Long placeFavoriteId
) {

    public static PlaceFavoriteResponse from(PlaceFavorite placeFavorite) {
        return new PlaceFavoriteResponse(
                placeFavorite.getId()
        );
    }
}
