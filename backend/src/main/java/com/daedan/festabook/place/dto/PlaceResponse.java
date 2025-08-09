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
        Long placeId,
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

    public static PlaceResponse fromWithDetail(
            Place place,
            PlaceDetail placeDetail,
            List<PlaceImage> images,
            List<PlaceAnnouncement> announcements
    ) {
        return new PlaceResponse(
                place.getId(),
                PlaceImageResponses.from(images),
                place.getCategory(),
                placeDetail.getTitle(),
                placeDetail.getStartTime(),
                placeDetail.getEndTime(),
                placeDetail.getLocation(),
                placeDetail.getHost(),
                placeDetail.getDescription(),
                PlaceAnnouncementResponses.from(announcements)
        );
    }

    public static PlaceResponse from(Place place) {
        return new PlaceResponse(
                place.getId(),
                PlaceImageResponses.from(List.of()),
                place.getCategory(),
                null,
                null,
                null,
                null,
                null,
                null,
                PlaceAnnouncementResponses.from(List.of())
        );
    }
}
