package com.daedan.festabook.announcement.dto;

import com.daedan.festabook.announcement.domain.Announcement;

public record AnnouncementUpdateResponse(
        Long announcementId,
        String title,
        String content
) {

    public static AnnouncementUpdateResponse from(Announcement announcement) {
        return new AnnouncementUpdateResponse(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getContent()
        );
    }
}
