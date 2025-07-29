package com.daedan.festabook.announcement.dto;

public record AnnouncementUpdateRequest(
        String title,
        String content
) {
}
