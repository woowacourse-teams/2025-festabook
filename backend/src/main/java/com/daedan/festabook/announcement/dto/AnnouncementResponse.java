package com.daedan.festabook.announcement.dto;

import com.daedan.festabook.announcement.domain.Announcement;
import java.time.LocalDateTime;

public record AnnouncementResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt
) {

    public static AnnouncementResponse from(Announcement announcement) {
        return new AnnouncementResponse(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getContent(),
                announcement.getCreatedAt()
        );
    }
}
