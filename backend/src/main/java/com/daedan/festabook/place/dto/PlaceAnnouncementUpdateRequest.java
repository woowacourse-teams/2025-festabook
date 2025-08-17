package com.daedan.festabook.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceAnnouncementUpdateRequest(

        @Schema(description = "플레이스 공지사항 제목", example = "긴급! 재료가 소진되었습니다.")
        String title,

        @Schema(description = "플레이스 공지사항 내용", example = "새우가 모두 소진되어 새우볶음밥은 판매하지 않습니다.")
        String content
) {
}
