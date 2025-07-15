package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.PlaceAnnouncement;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record PlaceAnnouncementResponses(
        @JsonValue List<PlaceAnnouncementResponse> responses
) {

    public static PlaceAnnouncementResponses from(List<PlaceAnnouncement> placeAnnouncements) {
        return new PlaceAnnouncementResponses(
                placeAnnouncements.stream()
                        .map(PlaceAnnouncementResponse::from)
                        .toList()
        );
    }
}
