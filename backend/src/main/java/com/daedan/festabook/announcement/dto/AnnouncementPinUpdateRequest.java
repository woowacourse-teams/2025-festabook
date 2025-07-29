package com.daedan.festabook.announcement.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AnnouncementPinUpdateRequest(

        @Schema(description = "공지 고정 여부", example = "false")
        boolean pinned
) {
}
