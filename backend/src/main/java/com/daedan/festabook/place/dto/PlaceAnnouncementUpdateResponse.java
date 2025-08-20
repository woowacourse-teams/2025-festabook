package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceAnnouncement;

public record PlaceAnnouncementUpdateResponse(
        Long id,
        String title,
        String content
) {

    public static PlaceAnnouncementUpdateResponse from(PlaceAnnouncement placeAnnouncement) {
        return new PlaceAnnouncementUpdateResponse(
                placeAnnouncement.getId(),
                placeAnnouncement.getTitle(),
                placeAnnouncement.getContent()
        );
    }
}
