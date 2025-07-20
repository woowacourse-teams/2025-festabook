package com.daedan.festabook.place.domain;

public enum PlaceCategory {
    BOOTH,
    BAR,
    FOOD_TRUCK,
    SMOKING,
    TRASH_CAN;

    public boolean isServiceLocation() {
        return this == BOOTH || this == BAR || this == FOOD_TRUCK;
    }
}
