package com.daedan.festabook.announcement.dto;

import com.daedan.festabook.announcement.domain.Announcement;
import com.daedan.festabook.festival.domain.Festival;
import io.swagger.v3.oas.annotations.media.Schema;

public record AnnouncementRequest(

        @Schema(description = "공지 제목", example = "폭우가 내립니다.")
        String title,

        @Schema(description = "공지 내용", example = "우산을 챙겨주세요.")
        String content,

        @Schema(description = "공지 고정 여부", example = "true")
        boolean isPinned
) {

    public Announcement toEntity(Festival festival) {
        return new Announcement(
                title,
                content,
                isPinned,
                festival
        );
    }
}
