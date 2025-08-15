package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceAnnouncement;
import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceAnnouncementRequest(

        @Schema(description = "공지 제목", example = "폭우가 내립니다.")
        String title,

        @Schema(description = "공지 내용", example = "우산을 챙겨주세요.")
        String content
) {

    public PlaceAnnouncement toEntity(Place place) {
        return new PlaceAnnouncement(
                place,
                title,
                content
        );
    }
}
