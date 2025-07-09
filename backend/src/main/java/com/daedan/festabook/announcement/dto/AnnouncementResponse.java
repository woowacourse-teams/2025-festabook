package com.daedan.festabook.announcement.dto;

import com.daedan.festabook.announcement.domain.Announcement;
import java.time.LocalDateTime;

public record AnnouncementResponse(
        Long id,
        String title,
        LocalDateTime createAt,
        String content
) {

    public static AnnouncementResponse from(Announcement announcement) {
        return new AnnouncementResponse(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getCreateAt(),
                announcement.getContent()
        );
    }
}
