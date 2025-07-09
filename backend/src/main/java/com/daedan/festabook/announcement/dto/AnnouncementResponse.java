package com.daedan.festabook.announcement.dto;

import com.daedan.festabook.announcement.domain.Announcement;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;

public record AnnouncementResponse(
        String title,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String content
) {

    public static AnnouncementResponse from(final Announcement announcement) {
        return new AnnouncementResponse(
                announcement.getTitle(),
                announcement.getDate(),
                announcement.getTime(),
                announcement.getContent()
        );
    }
}
