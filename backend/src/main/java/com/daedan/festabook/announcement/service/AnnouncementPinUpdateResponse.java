package com.daedan.festabook.announcement.service;

import com.daedan.festabook.announcement.domain.Announcement;

public record AnnouncementPinUpdateResponse(
        Long announcementId,
        boolean isPinned
) {

    public static AnnouncementPinUpdateResponse from(Announcement announcement) {
        return new AnnouncementPinUpdateResponse(
                announcement.getId(),
                announcement.isPinned()
        );
    }
}
