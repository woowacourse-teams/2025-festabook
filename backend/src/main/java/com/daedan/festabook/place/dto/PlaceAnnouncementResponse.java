package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceAnnouncement;
import java.time.LocalDateTime;

public record PlaceAnnouncementResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt
) {

    public static PlaceAnnouncementResponse from(PlaceAnnouncement placeAnnouncement) {
        return new PlaceAnnouncementResponse(
                placeAnnouncement.getId(),
                placeAnnouncement.getTitle(),
                placeAnnouncement.getContent(),
                placeAnnouncement.getCreatedAt()
        );
    }
}
