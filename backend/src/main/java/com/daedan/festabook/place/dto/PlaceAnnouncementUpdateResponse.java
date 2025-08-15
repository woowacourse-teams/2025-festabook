package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceAnnouncement;

public record PlaceAnnouncementUpdateResponse(
        String title,
        String content
) {

    public static PlaceAnnouncementUpdateResponse from(PlaceAnnouncement placeAnnouncement) {
        return new PlaceAnnouncementUpdateResponse(
                placeAnnouncement.getTitle(),
                placeAnnouncement.getContent()
        );
    }
}
