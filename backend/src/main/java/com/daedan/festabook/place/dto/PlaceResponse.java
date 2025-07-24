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
            PlaceDetail placeDetail,
            List<PlaceImage> images,
            List<PlaceAnnouncement> announcements
    ) {
        return new PlaceResponse(
                placeDetail.getPlace().getId(),
                PlaceImageResponses.from(images),
                placeDetail.getPlace().getCategory(),
                placeDetail.getTitle(),
                placeDetail.getStartTime(),
                placeDetail.getEndTime(),
                placeDetail.getLocation(),
                placeDetail.getHost(),
                placeDetail.getDescription(),
                PlaceAnnouncementResponses.from(announcements)
        );
    }

    public static PlaceResponse from(PlaceDetail placeDetail) {
        return new PlaceResponse(
                placeDetail.getPlace().getId(),
                null,
                placeDetail.getPlace().getCategory(),
                placeDetail.getTitle(),
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static PlaceResponse from(Place place) {
        return new PlaceResponse(
                place.getId(),
                null,
                place.getCategory(),
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
