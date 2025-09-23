package com.daedan.festabook.place.dto;

import com.daedan.festabook.festival.dto.FestivalCoordinateResponse;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.timetag.domain.TimeTag;
import com.daedan.festabook.timetag.dto.TimeTagResponses;
import java.util.List;

public record PlaceGeographyResponse(
        Long placeId,
        PlaceCategory category,
        FestivalCoordinateResponse markerCoordinate,
        String title,
        TimeTagResponses timeTags
) {

    public static PlaceGeographyResponse from(Place place, List<TimeTag> timeTags) {
        return new PlaceGeographyResponse(
                place.getId(),
                place.getCategory(),
                FestivalCoordinateResponse.from(place.getCoordinate()),
                place.getTitle(),
                TimeTagResponses.from(timeTags)
        );
    }
}
