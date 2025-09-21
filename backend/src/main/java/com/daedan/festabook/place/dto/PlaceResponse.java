package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.timetag.domain.TimeTag;
import com.daedan.festabook.timetag.dto.TimeTagResponses;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import java.util.List;

public record PlaceResponse(
        Long placeId,
        PlaceImageResponses placeImages,
        PlaceCategory category,
        String title,
        @JsonFormat(pattern = "HH:mm") LocalTime startTime,
        @JsonFormat(pattern = "HH:mm") LocalTime endTime,
        String location,
        String host,
        String description,
        PlaceAnnouncementResponses placeAnnouncements,
        TimeTagResponses timeTags
) {

    public static PlaceResponse from(
            Place place,
            List<PlaceImage> images,
            List<PlaceAnnouncement> announcements,
            List<TimeTag> timeTags
    ) {
        return new PlaceResponse(
                place.getId(),
                PlaceImageResponses.from(images),
                place.getCategory(),
                place.getTitle(),
                place.getStartTime(),
                place.getEndTime(),
                place.getLocation(),
                place.getHost(),
                place.getDescription(),
                PlaceAnnouncementResponses.from(announcements),
                TimeTagResponses.from(timeTags)
        );
    }

    public static PlaceResponse from(Place place) {
        return new PlaceResponse(
                place.getId(),
                PlaceImageResponses.from(List.of()),
                place.getCategory(),
                place.getTitle(),
                place.getStartTime(),
                place.getEndTime(),
                place.getLocation(),
                place.getHost(),
                place.getDescription(),
                PlaceAnnouncementResponses.from(List.of()),
                TimeTagResponses.from(List.of())
        );
    }
}
