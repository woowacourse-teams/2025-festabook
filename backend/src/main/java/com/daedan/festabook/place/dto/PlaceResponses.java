package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.timetag.domain.TimeTag;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import java.util.Map;

public record PlaceResponses(
        @JsonValue List<PlaceResponse> responses
) {

    public static PlaceResponses from(
            List<Place> places,
            Map<Long, List<PlaceImage>> placeImages,
            Map<Long, List<PlaceAnnouncement>> placeAnnouncements,
            Map<Long, List<TimeTag>> timeTags
    ) {
        return new PlaceResponses(
                places.stream()
                        .map(place -> PlaceResponse.from(
                                place,
                                placeImages.getOrDefault(place.getId(), List.of()),
                                placeAnnouncements.getOrDefault(place.getId(), List.of()),
                                timeTags.getOrDefault(place.getId(), List.of())
                        ))
                        .toList()
        );
    }
}
