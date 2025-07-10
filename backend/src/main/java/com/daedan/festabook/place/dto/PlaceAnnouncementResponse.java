package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;

public record PlaceAnnouncementResponse(
        Long id,
        String title,
        String content,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time
) {

    public static PlaceAnnouncementResponse from(PlaceAnnouncement placeAnnouncement) {
        return new PlaceAnnouncementResponse(
                placeAnnouncement.getId(),
                placeAnnouncement.getTitle(),
                placeAnnouncement.getContent(),
                placeAnnouncement.getDate(),
                placeAnnouncement.getTime()
        );
    }
}
