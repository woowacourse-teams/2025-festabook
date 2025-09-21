package com.daedan.festabook.timetag.domain;

import com.daedan.festabook.place.domain.Place;

public class PlaceTimeTagFixture {

    public static PlaceTimeTag createWithPlaceAndTimeTag(Place place, TimeTag originalTimeTag) {
        return new PlaceTimeTag(place, originalTimeTag);
    }
}
