package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.daedan.festabook.place.domain.PlaceCategory;
import com.daedan.festabook.place.domain.PlaceDetail;
import com.daedan.festabook.place.domain.PlaceImage;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import java.util.List;

public record PlaceResponse(
        Long id,
        PlaceImageResponses placeImages,
        PlaceCategory category,
        String title,
        @JsonFormat(pattern = "HH:mm") LocalTime startTime,
        @JsonFormat(pattern = "HH:mm") LocalTime endTime,
        String location,
        String host,
        String description,
        PlaceAnnouncementResponses placeAnnouncements
) {

    public static PlaceResponse from(
            Place place,
            PlaceDetail details,
            List<PlaceImage> images,
            List<PlaceAnnouncement> announcements
    ) {
        return new PlaceResponse(
                place.getId(),
                PlaceImageResponses.from(images),
                place.getCategory(),
                details.getTitle(),
                details.getStartTime(),
                details.getEndTime(),
                details.getLocation(),
                details.getHost(),
                details.getDescription(),
                PlaceAnnouncementResponses.from(announcements)
        );
    }
}
