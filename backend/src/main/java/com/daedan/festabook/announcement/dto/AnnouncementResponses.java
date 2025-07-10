package com.daedan.festabook.announcement.dto;

import com.daedan.festabook.announcement.domain.Announcement;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record AnnouncementResponses(
        @JsonValue List<AnnouncementResponse> announcementResponses
) {

    public static AnnouncementResponses from(List<Announcement> announcements) {
        return new AnnouncementResponses(
                announcements.stream()
                        .map(AnnouncementResponse::from)
                        .toList()
        );
    }
}
